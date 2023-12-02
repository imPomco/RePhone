package com.example.rephone.Fragment;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rephone.Activity.MainActivity;
import com.example.rephone.R;
import com.example.rephone.Thread.DBThread;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class OldPhoneHomeFragment extends Fragment {
    ConstraintLayout homeLayout;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Bitmap prevBitmap, currBitmap;
    private Timer timer;
    private int diff, volume = 0;
    private String fileName;
    private MediaRecorder mediaRecorder;
    private TextView textView1, textView2;
    private boolean isRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_old_phone_home, container, false);

        Button startButton = view.findViewById(R.id.old_phone_home_start_button);
        previewView = view.findViewById(R.id.old_phone_home_preview_view);

        fileName = requireActivity().getCacheDir().getAbsolutePath() + "/audio.3gp";
        textView1 = view.findViewById(R.id.old_phone_home_textview_1);
        textView2 = view.findViewById(R.id.old_phone_home_textview_2);
        homeLayout = view.findViewById(R.id.old_phone_home_layout);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                }
            }, ContextCompat.getMainExecutor(requireContext()));
        } else
            requireActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            mediaRecorder = new MediaRecorder();
        else
            requireActivity().requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences functionSettings = requireActivity().getSharedPreferences("functionSettings", Context.MODE_PRIVATE);

                int diffValue = (int) (40 - functionSettings.getFloat("motionSliderValue", 20));
                int volumeValue = (int) (36863 - functionSettings.getFloat("soundSliderValue", 16384));
                boolean screenValue = functionSettings.getBoolean("isScreenSwitchChecked", false);
                String motion = functionSettings.getBoolean("isMotionSwitchChecked", true) + "";
                String sound = functionSettings.getBoolean("isSoundSwitchChecked", true) + "";
                String token = ((MainActivity) requireActivity()).savedToken.getString("token", "");

                if (isRunning) {
                    if (screenValue)
                        homeLayout.setBackgroundColor(Color.TRANSPARENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && screenValue) {
                        WindowInsetsController controller = requireView().getWindowInsetsController();
                        controller.show(WindowInsetsCompat.Type.systemBars());
                    } else if (screenValue)
                        ((MainActivity) requireActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    startButton.setBackgroundResource(R.drawable.image_power_off);
                    ((MainActivity) requireActivity()).mainMenuNavigation.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).mainTitleBar.setVisibility(View.VISIBLE);
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    textView1.setText("동작 상태가 아닙니다.");
                    textView2.setText("아래 버튼을 눌러 시작하세요.");

                    if (functionSettings.getBoolean("isMotionSwitchChecked", true))
                        cameraProvider.unbindAll();
                    if (functionSettings.getBoolean("isSoundSwitchChecked", true))
                        releaseMediaRecorder();

                    timer.cancel();

                    try {
                        DBThread db = new DBThread("set_function", token, motion, sound, "false");
                        db.start();
                        db.join(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    isRunning = !isRunning;
                } else {
                    if (((MainActivity) requireActivity()).checkSavedToken()) {
                        if (screenValue) {
                            Toast.makeText(requireActivity(), "화면 중앙을 눌러 동작을 중지할 수 있습니다.", Toast.LENGTH_LONG).show();
                            homeLayout.setBackgroundColor(Color.BLACK);
                            startButton.setBackgroundColor(Color.BLACK);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && screenValue) {
                            WindowInsetsController controller = requireView().getWindowInsetsController();
                            controller.hide(WindowInsetsCompat.Type.systemBars());
                        } else if (screenValue)
                            ((MainActivity) requireActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
                        else
                            startButton.setBackgroundResource(R.drawable.image_power_on);

                        ((MainActivity) requireActivity()).mainMenuNavigation.setVisibility(View.GONE);
                        ((MainActivity) requireActivity()).mainTitleBar.setVisibility(View.GONE);
                        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        textView1.setText("동작 중");
                        textView2.setText("중단하려면 아래 버튼을 누르세요.");

                        if (functionSettings.getBoolean("isMotionSwitchChecked", true))
                            bindPreview(cameraProvider);
                        if (functionSettings.getBoolean("isSoundSwitchChecked", true))
                            initMediaRecorder();

                        try {
                            DBThread db = new DBThread("set_function", token, motion, sound, "true");
                            db.start();
                            db.join(2000);

                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (functionSettings.getBoolean("isMotionSwitchChecked", true)) {
                                        capturePhoto();
                                        if (prevBitmap != null && currBitmap != null)
                                            diff = getDifference(prevBitmap, currBitmap);
                                        if (diff >= diffValue) {
                                            try {
                                                DBThread db = new DBThread("set_log", token, "motion");
                                                db.start();
                                                db.join(500);
                                                Log.d("MOTION_DETECT", "움직임 감지됨");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.d("MOTION_DETECT", diff + "");
                                    }

                                    if (functionSettings.getBoolean("isSoundSwitchChecked", true)) {
                                        volume = audioVolumeCheck();
                                        if (volume >= volumeValue) {
                                            try {
                                                DBThread db = new DBThread("set_log", token, "sound");
                                                db.start();
                                                db.join(500);
                                                Log.d("AUDIO_DETECT", "소리 감지됨");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }, 0, 500);
                            isRunning = !isRunning;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        return view;
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(requireActivity(), cameraSelector, preview, imageCapture);
    }

    private void capturePhoto() {
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                if (currBitmap == null)
                    currBitmap = Bitmap.createScaledBitmap(image.toBitmap(), 10, 10, true);
                else {
                    prevBitmap = currBitmap;
                    currBitmap = Bitmap.createScaledBitmap(image.toBitmap(), 10, 10, true);
                }
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }

    private void initMediaRecorder() {
        if (mediaRecorder == null)
            mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setMaxDuration(60000);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            mediaRecorder.release();
            Log.d("RECORD_AUDIO", "녹음 준비 실패");
        }
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder media, int i, int i1) {
                if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    initMediaRecorder();
                }
            }
        });
    }

    private void releaseMediaRecorder() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private int audioVolumeCheck() {
        if (mediaRecorder != null) {
            int volume = mediaRecorder.getMaxAmplitude();
            Log.d("RECORD_AUDIO", volume + "");
            return volume;
        }
        return 0;
    }

    private int getDifference(Bitmap prevBitmap, Bitmap currBitmap) {
        long diff = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                diff += getPixelDiff(prevBitmap.getPixel(i, j), currBitmap.getPixel(i, j));
        long maxDiff = 3L * 255 * 10 * 10;
        return (int) (100 * diff / maxDiff) * 3;
    }

    private int getPixelDiff(int rgb1, int rgb2) {
        int r1 = rgb1 >> 16 & 0xff;
        int g1 = rgb1 >> 8 & 0xff;
        int b1 = rgb1 & 0xff;

        int r2 = rgb2 >> 16 & 0xff;
        int g2 = rgb2 >> 8 & 0xff;
        int b2 = rgb2 & 0xff;

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaRecorder();
    }
}