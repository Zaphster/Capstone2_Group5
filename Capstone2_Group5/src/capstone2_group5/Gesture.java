/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstone2_group5;

import com.leapmotion.leap.*;

/**
 *
 * @author Cameron
 */
public class Gesture extends DecisionTreeNode{
    public static Boolean debug = Capstone2_Group5.debug;
    public static RadianRange allPositions;
    static{
        try{
            allPositions = new RadianRange(new Float(0), new Float(180));
        }catch(Exception e){
            Debugger.print(e.getMessage());
        }
    }
    
    public String name;
    public Image rawImage;
    
    public GestureFinger index;
    public GestureFinger middle;
    public GestureFinger ring;
    public GestureFinger pinky;
    public GestureFinger thumb;
    
    public GesturePalm palm;
    
    public RadianRange grabAngleAllowedPositions;
    public DistanceRange pinchDistanceAllowedPositions;
    
    public Gesture(){
        index = new GestureFinger(Finger.Type.TYPE_INDEX);
        middle = new GestureFinger(Finger.Type.TYPE_MIDDLE);
        ring = new GestureFinger(Finger.Type.TYPE_RING);
        pinky = new GestureFinger(Finger.Type.TYPE_PINKY);
        thumb = new GestureFinger(Finger.Type.TYPE_THUMB);
        palm = new GesturePalm();
    }
    

    
    public Boolean performedIn(Frame frame){
        Debugger.print("Checking if " + name + " is in frame " + frame);
        Hand hand = frame.hands().frontmost();
        if(hand.isValid() == false){
            return false;
        }
//        System.out.println("hand " + hand);
        Finger frameIndex = hand.fingers().fingerType(Finger.Type.TYPE_INDEX).get(0);
//        System.out.println("index " + frameIndex);
        Finger frameMiddle = hand.fingers().fingerType(Finger.Type.TYPE_MIDDLE).get(0);
//        System.out.println("middle " + frameMiddle);
        Finger frameRing = hand.fingers().fingerType(Finger.Type.TYPE_RING).get(0);
//        System.out.println("ring " + frameRing);
        Finger framePinky = hand.fingers().fingerType(Finger.Type.TYPE_PINKY).get(0);
//        System.out.println("pinky " + framePinky);
        Finger frameThumb = hand.fingers().fingerType(Finger.Type.TYPE_THUMB).get(0);
//        System.out.println("thumb " + frameThumb);
        if(!frameIndex.isValid() || !frameMiddle.isValid() || !frameRing.isValid() || !framePinky.isValid() || !frameThumb.isValid()){
            return false;
        }
        Boolean indexCorrect = this.index.performedBy(frameIndex);
        Debugger.print("  Index correct: " + indexCorrect);
        Boolean middleCorrect = this.middle.performedBy(frameMiddle);
        Debugger.print("  Middle correct: " + middleCorrect);
        Boolean ringCorrect = this.ring.performedBy(frameRing);
        Debugger.print("  Ring Correct: " + ringCorrect);
        Boolean pinkyCorrect = this.pinky.performedBy(framePinky);
        Debugger.print("  Pinky Correct: " + pinkyCorrect);
        Boolean thumbCorrect = this.thumb.performedBy(frameThumb);
        Debugger.print("  Thumb Correct: " + thumbCorrect);
        Boolean palmCorrect = this.palm.allowedVector.contains(hand.palmNormal());
        Debugger.print("  Palm correct:  " + palmCorrect);
        
        Boolean performed = (indexCorrect &&
                middleCorrect &&
                ringCorrect &&
                pinkyCorrect &&
                thumbCorrect &&
                palmCorrect);
        return performed;
    }
    
    public void setFingerCenter(Finger.Type type, Vector newCenter) throws Exception{
        getGestureFinger(type).allowedDirection.setCenter(newCenter);
    }
    
    public void setFingerBoneCenter(Finger.Type fingerType, Bone.Type boneType, Vector center) throws Exception{
        getGestureBone(fingerType, boneType).allowedDirection.setCenter(center);
    }
    
    public void setFingerRange(Finger.Type type, Float newRange) throws Exception{
        getGestureFinger(type).allowedDirection.setAllRanges(newRange);
    }
    
    public void setFingerBoneRange(Finger.Type fingerType, Bone.Type boneType, Float range) throws Exception{
        if(!(fingerType == Finger.Type.TYPE_THUMB && boneType == Bone.Type.TYPE_METACARPAL)){
            getGestureBone(fingerType, boneType).allowedDirection.setAllRanges(range);
        }
    }
    
    public void setAllFingerBoneRanges(Finger.Type fingerType, Float range) throws Exception{
        for(Bone.Type type : Bone.Type.values()){
            setFingerBoneRange(fingerType, type, range);
        }
    }
    
    public void setAllHandBoneRanges(Float range) throws Exception{
        for(Finger.Type fingerType : Finger.Type.values()){
            setAllFingerBoneRanges(fingerType, range);
        }
    }
    
    public void setPalmCenter(Vector center) throws Exception{
        this.palm.allowedVector.setCenter(center);
    }
    
    public void setPalmRange(Float range) throws Exception{
        this.palm.allowedVector.setAllRanges(range);
    }
    
    public void setGrabCenter(Float center) throws Exception{
        this.grabAngleAllowedPositions.setCenter(center);
    }
    
    public void setGrabRange(Float range) throws Exception{
        this.grabAngleAllowedPositions.setRange(range);
    }
    
    private GestureFinger getGestureFinger(Finger.Type type) throws Exception{
        GestureFinger finger;
        switch(type){
            case TYPE_THUMB:
                finger = this.thumb;
                break;
            case TYPE_INDEX:
                finger = this.index;
                break;
            case TYPE_MIDDLE:
                finger = this.middle;
                break;
            case TYPE_RING:
                finger = this.ring;
                break;
            case TYPE_PINKY:
                finger = this.pinky;
                break;
            default:
                throw new Exception("Unable to find gesture finger <" + type + ">");
        }
        return finger;
    }
    
    private GestureBone getGestureBone(Finger.Type fingerType, Bone.Type boneType)throws Exception{
        GestureFinger finger = getGestureFinger(fingerType);
        if(finger != null){
            GestureBone bone;
            switch(boneType){
                case TYPE_DISTAL:
                    bone = finger.distal;
                    break;
                case TYPE_PROXIMAL:
                    bone = finger.proximal;
                    break;
                case TYPE_INTERMEDIATE:
                    bone = finger.intermediate;
                    break;
                case TYPE_METACARPAL:
                    bone = finger.metacarpal;
                    break;
                default:
                    throw new Exception("Unable to find gesture bone <" + fingerType + "> <" + boneType + ">");
            }
            return bone;
        } else {
            throw new Exception("Unable to find gesture bone <" + fingerType + "> <" + boneType + ">");
        }
    }
    
    @Override
    public Boolean isGesture(){
        return true;
    }
    
    @Override
    public String toString(){
        return "  Index: {\n" +
               "    isExtended: " + index.isExtended + ",\n" +
               "    allowedPositions: " + index.allowedDirection + ",\n" +
               "    bonePositions: {\n" +
               "      metacarpal: " + index.metacarpal.allowedDirection + ",\n" +
               "      proximal: " + index.proximal.allowedDirection + ",\n" +
               "      intermediate: " + index.intermediate.allowedDirection + ",\n" +
               "      distal: " + index.distal.allowedDirection + "},\n" +
               "  },\n" +
               "  Middle: {\n" +
               "    isExtended: " + middle.isExtended + ",\n" +
               "    allowedPositions: " + middle.allowedDirection + "},\n" +
               "    bonePositions: {\n" +
               "      metacarpal: " + middle.metacarpal.allowedDirection + ",\n" +
               "      proximal: " + middle.proximal.allowedDirection + ",\n" +
               "      intermediate: " + middle.intermediate.allowedDirection + ",\n" +
               "      distal: " + middle.distal.allowedDirection + "},\n" +
               "  },\n" +
               "  Ring: {\n" +
               "    isExtended: " + ring.isExtended + ",\n" +
               "    allowedPositions: " + ring.allowedDirection + "},\n" +
               "    bonePositions: {\n" +
               "      metacarpal: " + ring.metacarpal.allowedDirection + ",\n" +
               "      proximal: " + ring.proximal.allowedDirection + ",\n" +
               "      intermediate: " + ring.intermediate.allowedDirection + ",\n" +
               "      distal: " + ring.distal.allowedDirection + "},\n" +
               "  },\n" +
               "  Pinky: {\n" +
               "    isExtended: " + pinky.isExtended + ",\n" +
               "    allowedPositions: " + pinky.allowedDirection + "},\n" +
               "    bonePositions: {\n" +
               "      metacarpal: " + pinky.metacarpal.allowedDirection + ",\n" +
               "      proximal: " + pinky.proximal.allowedDirection + ",\n" +
               "      intermediate: " + pinky.intermediate.allowedDirection + ",\n" +
               "      distal: " + pinky.distal.allowedDirection + "},\n" +
               "  },\n" +
               "  Thumb: {\n" +
               "    isExtended: " + thumb.isExtended + ",\n" +
               "    allowedPositions: " + thumb.allowedDirection + "},\n" +
               "    bonePositions: {\n" +
               "      metacarpal: " + thumb.metacarpal.allowedDirection + ",\n" +
               "      proximal: " + thumb.proximal.allowedDirection + ",\n" +
               "      intermediate: " + thumb.intermediate.allowedDirection + ",\n" +
               "      distal: " + thumb.distal.allowedDirection + "},\n" +
               "  },\n" +
               "  Palm Normal: " + palm.allowedVector + ",\n" +
               "  Grab Angle: " + this.grabAngleAllowedPositions + ",\n" +
               "  Pinch Distance: " + this.pinchDistanceAllowedPositions;
    }
}