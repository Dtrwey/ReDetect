#include <iostream>
#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <dlib/opencv.h>
#include <dlib/svm_threaded.h>
#include <dlib/data_io.h>
#include <dlib/image_processing/frontal_face_detector.h>
#include <dlib/image_processing/render_face_detections.h>
#include <dlib/image_processing.h>
#include <dlib/gui_widgets.h>
#include <dlib/image_keypoint.h>

using namespace cv;
using namespace dlib;
using namespace std;

#ifndef _Included_com_ReDetect_redetect_NativeClass
#define _Included_com_ReDetect_redetect_NativeClass
#ifdef __cplusplus
extern "C" {
#endif

void recyclingDetectionDlib(Mat& img, Mat& dst);
void renderToMat(std::vector<dlib::rectangle>& dets, Mat& dst);
void renderToMatNon(std::vector<dlib::rectangle>& dets, Mat& dst);
void renderToMatSpecial(std::vector<dlib::rectangle>& dets, Mat& dst);

/*
 * Class:     com_ReDetect_redetect_NativeClass
 * Method:    getMessage
 */
JNIEXPORT jstring JNICALL Java_com_ReDetect_redetect_NativeClass_getMessage
  (JNIEnv *, jclass);

/*
 * Class:     com_ReDetect_redetect_NativeClass
 * Method:    LandmarkDetection
 */
JNIEXPORT void JNICALL Java_com_ReDetect_redetect_NativeClass_LandmarkDetection
  (JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
