#include <com_ReDetect_redetect_NativeClass.h>
#include "com_ReDetect_redetect_NativeClass.h"
#include <iostream>
#include <jni.h>

JNIEXPORT jstring JNICALL Java_com_ReDetect_redetect_NativeClass_getMessage
  (JNIEnv *env, jclass){
	env->NewStringUTF("JNI message");
  }
  
JNIEXPORT void JNICALL Java_com_ReDetect_redetect_NativeClass_LandmarkDetection
	(JNIEnv *env, jclass thiz, jlong addrInput, jlong addrOutput){
		Mat& image = *(Mat*)addrInput;
		Mat& dst = *(Mat*)addrOutput;
		
		faceDetectionDlib(image,dst);
	}
	
void recyclingDetectionDlib(Mat& img, Mat& dst){
	
	try{
		typedef scan_fhog_pyramid<pyramid_down<6> > image_scanner_type; 
		
		object_detector<image_scanner_type> bottle_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/bottle-22.svm") >> bottle_detector;
		
		object_detector<image_scanner_type> cans_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/cans-18.svm") >> cans_detector;
		
		object_detector<image_scanner_type> cartons_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/cartons-9.svm") >> cartons_detector;
		
		object_detector<image_scanner_type> gatorade_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/gatorade-9.svm") >> gatorade_detector;
		
		object_detector<image_scanner_type> glass_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/glass-11.svm") >> glass_detector;
		
		object_detector<image_scanner_type> jugs_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/jugs-6.svm") >> jugs_detector;
	
		object_detector<image_scanner_type> soda_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/soda-11.svm") >> soda_detector;
		
		object_detector<image_scanner_type> tall_soda_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/tall-sodas-11.svm") >> tall_soda_detector;
		
		object_detector<image_scanner_type> paper_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/paper-8.svm") >> paper_detector;
		
		object_detector<image_scanner_type> detergent_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/detergent-8.svm") >> detergent_detector;
		
		object_detector<image_scanner_type> egg_shells_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/egg_shells-7.svm") >> egg_shells_detector;
		
		object_detector<image_scanner_type> foil_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/foil-11.svm") >> foil_detector;
		
		object_detector<image_scanner_type> burrito_detector;
		deserialize("/data/user/0/com.ReDetect.redetect/files/burrito-2.svm") >> burrito_detector;
		
		cv_image<bgr_pixel> cimg(img);
		
		std::vector<object_detector<image_scanner_type> > my_detectors;
		my_detectors.push_back(bottle_detector);
		my_detectors.push_back(cans_detector);
		my_detectors.push_back(cartons_detector);
		my_detectors.push_back(gatorade_detector);
		my_detectors.push_back(glass_detector);
		my_detectors.push_back(jugs_detector);
		my_detectors.push_back(soda_detector);
		my_detectors.push_back(tall_soda_detector);
		my_detectors.push_back(paper_detector);
		my_detectors.push_back(detergent_detector);
		my_detectors.push_back(egg_shells_detector);
		my_detectors.push_back(foil_detector);
		my_detectors.push_back(burrito_detector);
		
		std::vector<dlib::rectangle> dets = evaluate_detectors(my_detectors, cimg);

		dst = img.clone();
		
		renderToMat(dets,dst);
		
		if(dets.size() == 0){
			string text = "Nothing Recyclable";
			int fontFace = FONT_HERSHEY_DUPLEX;
			double fontScale = 1.0;
			int thickness = 3;
			Scalar color = Scalar(51,51,255); 
			
			Size textSize = cv::getTextSize(text, fontFace, fontScale, thickness, 0);
			
			Point textOrg((img.cols - textSize.width)/2,(img.rows + textSize.height)/2);
			
			cv::putText(dst, text, textOrg, fontFace, fontScale, color, thickness);
		}
		
	}
	catch(serialization_error& e){
		cout << endl << e.what() << endl;
	}
}


void renderToMat(std::vector<dlib::rectangle>& dets, Mat& dst){
	Scalar color;
	int thickness;

	color = Scalar(51,255,51);
	thickness = 3;
	
	for(unsigned long idx = 0; idx < dets.size(); idx++){
		int x1 = dets[idx].left();
		int x2 = dets[idx].right() + 1;
		int y1 = dets[idx].top();
		int y2 = dets[idx].bottom() + 1;
		
		cv::rectangle(dst, Point(x1 , y1), Point(x2, y2), color, thickness);
		cv::putText(dst,"Recyclable", Point(x1 , y1), cv::FONT_HERSHEY_DUPLEX,1.0, color, 2);
        
	}
	
}

void renderToMatNon(std::vector<dlib::rectangle>& dets, Mat& dst){
	Scalar color;
	int thickness;

	color = Scalar(51,51,255);
	thickness = 3;
	
	for(unsigned long idx = 0; idx < dets.size(); idx++){
		int x1 = dets[idx].left();
		int x2 = dets[idx].right() + 1;
		int y1 = dets[idx].top();
		int y2 = dets[idx].bottom() + 1;
		
		cv::rectangle(dst, Point(x1 , y1), Point(x2, y2), color, thickness);
		cv::putText(dst,"Not Recyclable", Point(x1 , y1), cv::FONT_HERSHEY_DUPLEX,1.0, color, 2);
        
	}
}

void renderToMatSpecial(std::vector<dlib::rectangle>& dets, Mat& dst){
	Scalar color;
	int thickness;

	color = Scalar(51,255,255);
	thickness = 3;
	
	for(unsigned long idx = 0; idx < dets.size(); idx++){
		int x1 = dets[idx].left();
		int x2 = dets[idx].right() + 1;
		int y1 = dets[idx].top();
		int y2 = dets[idx].bottom() + 1;
		
		cv::rectangle(dst, Point(x1 , y1), Point(x2, y2), color, thickness);
		cv::putText(dst,"Recycle at your local grocery store", Point(x1 , y1), cv::FONT_HERSHEY_DUPLEX,0.5, color, 2);
        
	}
	
}

