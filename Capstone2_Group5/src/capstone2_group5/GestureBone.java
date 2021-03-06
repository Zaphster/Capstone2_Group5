/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstone2_group5;

import com.leapmotion.leap.Bone;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Cameron
 */
public class GestureBone implements JSONWritableReadable{
    public static Boolean debug = Capstone2_Group5.debug;
    public Bone.Type type;
    public VectorRange allowedDirection = new VectorRange();
    
    public GestureBone(){
        //prevent calling constructor with no type
//        allowedDirection = new VectorRange();
    }
            
    public GestureBone(Bone.Type type){
        this.type = type;
    }
    
    public Bone.Type getType(){
        return type;
    }

    @Override
    public void makeSelfFromJSON(String json) {
        Object obj = JSONValue.parse(json);
        if(obj != null){
            JSONObject jsonObj = (JSONObject)obj;
            makeSelfFromJSONObject(jsonObj);
        }
    }
    
    @Override
    public String makeJSONString(){
        return toJSONObject().toJSONString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("type", type.toString());
        obj.put("allowedDirection", allowedDirection.toJSONObject());
        return obj;
    }

    @Override
    public void makeSelfFromJSONObject(JSONObject jsonObject) {
        type = Bone.Type.valueOf(jsonObject.get("type").toString());
        allowedDirection.makeSelfFromJSONObject((JSONObject)jsonObject.get("allowedDirection"));
    }
}
