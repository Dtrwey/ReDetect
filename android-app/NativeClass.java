package com.ReDetect.redetect;

//Functions in C++.
public class NativeClass {
    public native static String getMessage();
    public native static void LandmarkDetection(long addrInput, long addrOutput);
}
