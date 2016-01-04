package com.push.m.record;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.media.MediaRecorder;
import android.util.Log;

import com.push.m.Constants;
import com.push.m.chat.IMUtil;

public class AudioRecorder implements RecordImp {
	private static final String TAG = "IMMessage-AudioRecord";

	private MediaRecorder recorder;
	private String fileName;

	private boolean isRecording = false;
	
	private long startRecordTime = 0, endRecordTime = 0;

	@Override
	public void ready() {
		// TODO Auto-generated method stub
		File file = new File(Constants.AUDIO_DIR);
		if (!file.exists()) {
			file.mkdir();
		}
		fileName = IMUtil.getCurrentDate();
		recorder = new MediaRecorder();
		recorder.setOutputFile(Constants.AUDIO_DIR + fileName + ".amr");
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (!isRecording) {
			try {
				Date date = new Date();
				recorder.prepare();
				recorder.start();
				startRecordTime = date.getTime();
			} catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}

			isRecording = true;
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (isRecording) {
			Date date = new Date();
			endRecordTime = date.getTime();
			recorder.stop();
			recorder.release();
			isRecording = false;
		}

	}

	@Override
	public void deleteOldFile() {
		// TODO Auto-generated method stub
		File file = new File(Constants.AUDIO_DIR + fileName + ".amr");
		if(file.exists()) {
			file.delete();
		}
	}

	@Override
	public double getAmplitude() {
		// TODO Auto-generated method stub
		if (!isRecording) {
			return 0;
		}
		return recorder.getMaxAmplitude();
	}

	@Override
	public String getFilePath() {
		return Constants.AUDIO_DIR + fileName + ".amr";
	}

	@Override
	public float getDuration() {
		return endRecordTime - startRecordTime;
	}

}
