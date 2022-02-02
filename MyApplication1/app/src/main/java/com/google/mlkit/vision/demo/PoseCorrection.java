package com.google.mlkit.vision.demo;

import android.speech.tts.TextToSpeech;

import com.google.mlkit.vision.demo.java.LivePreviewActivity;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.textToSpeech;
import static java.lang.Math.atan2;

public class PoseCorrection{

    private MyWorkerThread mWorkerThread;
    public static Pose pose;

    private static boolean isSaid;
    
    public PoseCorrection(String poseName){
        mWorkerThread = new MyWorkerThread(poseName);
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        isSaid = false;
        pose = null;
    }

    public void startCorrecting(){

        Runnable task = new Runnable() {
            @Override
            public void run() {

                while(true) {
                    //Order of angles
                    //rightHipAngle
                    //leftHipAngle
                    //rightKneeAngle
                    //leftKneeAngle
                    //rightShoulderAngle
                    //leftShoulderAngle
                    //rightElbowAngle
                    //leftElbowAngle

                    if(pose == null) {
                        continue;
                    }

                    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
                    if (landmarks.isEmpty()) {
                        continue;
                    }
                    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
                    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
                    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
                    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
                    PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
                    PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
                    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
                    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
                    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
                    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

                    double[] userAngles = new double[8];

                    userAngles[0] = getAngle(rightShoulder, rightHip, rightKnee);
                    userAngles[1] = getAngle(leftShoulder, leftHip, leftKnee);
                    userAngles[2] = getAngle(rightHip, rightKnee, rightAnkle);
                    userAngles[3] = getAngle(leftHip, leftKnee, leftAnkle);
                    userAngles[4] = getAngle(rightElbow, rightShoulder, rightHip);
                    userAngles[5] = getAngle(leftElbow, leftShoulder, leftHip);
                    userAngles[6] = getAngle(rightWrist, rightElbow, rightShoulder);
                    userAngles[7] = getAngle(leftWrist, leftElbow, leftShoulder);


                    if(!isSaid){
                        isSaid = true;
                        textToSpeech.speak("Stand straight with arms in resting position, the distance between your foots should be 3-4 feet. While exhaling, lift your arms in front of you parallel to the floor. Now spread them shoulder wide with palms facing down.",TextToSpeech.QUEUE_ADD,null);
                        textToSpeech.speak("Turn your left foot to the left making an angle of 45-60 degrees , align the left heel with the right heel", TextToSpeech.QUEUE_ADD, null);
                        textToSpeech.speak("Now while exhaling,  move your right knee cap outwards such that it is directly above ur right angle and  if possible, try to bring ur right thigh parallel to the floor. strengthen this pose by pushing the outer left heel firmly to the floor ", TextToSpeech.QUEUE_ADD, null);
                        textToSpeech.speak("Now stretch the arms away from the space between the shoulder blades. Do not lean your upper body  over your right thigh. now , turn your head towards the right and look over your fingers.", TextToSpeech.QUEUE_ADD, null);
                        textToSpeech.speak("Hold this pose for 30 seconds to 1 minute. Inhale to come up. Now repeat this for the left side for the same time period.", TextToSpeech.QUEUE_ADD, null);

                        while(textToSpeech.isSpeaking()){

                        }
                    }

                    angleCheck(userAngles);
                    try {
                        synchronized (mWorkerThread) {
                            mWorkerThread.wait(5000);
                            System.out.println("Workin fine");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Error");
                    }
                }
            }
        };

        mWorkerThread.postTask(task);
    }

    private double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                                lastPoint.getPosition().x - midPoint.getPosition().x)
                                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                                firstPoint.getPosition().x - midPoint.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
          result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    private double angleCheck(double userAngle[]) {
        double error, errorAvg = 0;

        for (int i = 0; i < 8; i++) {
            if (userAngle[i] > LivePreviewActivity.poseAngles[i]) {
                error = ((userAngle[i] - LivePreviewActivity.poseAngles[i]) / LivePreviewActivity.poseAngles[i]) * 100;
                errorAvg = errorAvg + error;

                System.out.print(i + " " + error + " ## " + userAngle[i] + " ## " + LivePreviewActivity.poseAngles[i]);
            } else {
                error = ((LivePreviewActivity.poseAngles[i] - userAngle[i]) / LivePreviewActivity.poseAngles[i]) * 100;
                errorAvg = errorAvg + error;
                System.out.print(i + " " + error + " ## " + userAngle[i] + " ## " + LivePreviewActivity.poseAngles[i]);
            }

            if(i == 0 && error > 10.0) {
                if (userAngle[i] > LivePreviewActivity.poseAngles[i]) {
                    textToSpeech.speak("Bend your Right knee a little bit.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Right knee a little bit.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 1 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Expand your left leg.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Shrink your left leg.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 2 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Bend your Right knee a little bit.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Right knee a little bit.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 3 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Expand your left leg.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Shrink your left leg.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 4 && error > 10.0) {
                
                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Lower your Right hand from shoulder.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Right hand from shoulder.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 5 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Lower your Left hand from shoulder.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Left hand from shoulder.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 6 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Lower your Right hand from ankle.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Right hand from ankle.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

            if(i == 7 && error > 10.0) {

                if(userAngle[i] > LivePreviewActivity.poseAngles[i]){
                    textToSpeech.speak("Lower your Left hand from ankle.", TextToSpeech.QUEUE_ADD, null);
                } else {
                    textToSpeech.speak("Raise your Left hand from ankle.", TextToSpeech.QUEUE_ADD, null);
                }

                return -1;
            }

          System.out.println();
        }

        errorAvg = errorAvg / 8;

        return errorAvg;
    }
}