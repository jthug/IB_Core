package com.lianer.core.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;


/**
 *
 * 提供json转换
 * @version 0.1
 */
public class GsonUtil {

    private static final String TAG = GsonUtil.class.getSimpleName();

    public static final Gson gson = new Gson();

    /**
     * 将对象转为字符串
     * @param obj 要转换的对象
     * @return 返回转换后的字符串
     */
    public static String toJsonString(Object obj){
        if(obj == null){
            return "";
        }
        return gson.toJson(obj);
    }

    /**
     * 将json字符串解析为对象
     * @param json 原json字符串
     * @param classOfT 解析对象得类
     * @param <T>
     * @return 返回解析后的对象
     */
    public static <T> T fromJson(String json,Class<T> classOfT){
        if(json == null || json.length() <1){
            return null;
        }

        return gson.fromJson(json,classOfT);
    }

    /**
     * 根据key获取json字符串对应的值
     * @param json  原json字符串
     * @param key 键值
     * @return 返回key对应的值
     */
    public static String fromJsonString(String json,String key){
        try {
            JsonObject jo = gson.fromJson(json,JsonObject.class);
            JsonElement ele = jo.get(key);
            if(ele != null && !ele.toString().equals("{}")){
                return  ele.toString();
            }
        } catch (JsonSyntaxException e) {
            LogBus.e(TAG,"",e);
        }
        return "";
    }

    public static String fromJSJsonString(String json,String key){
        try {
            JsonObject jo = gson.fromJson(json,JsonObject.class);
            JsonElement ele = jo.get(key);
            if(ele != null && !ele.toString().equals("{}")){
                return  ele.getAsString();
            }
        } catch (JsonSyntaxException e) {
            LogBus.e(TAG,"",e);
        }
        return "";
    }

//    /**
//     * 根据key获取json字符串对应的值
//     * @param json  原json字符串
//     * @param key 键值
//     * @return 返回key对应的值
//     */
//    public static String getJSParameter(String json,String key){
//        try {
//            JsonObject jo = gson.fromJson(json,JsonObject.class);
//            JsonObject par = jo.getAsJsonObject(JSKey.JS_PARAMETER);
//            JsonElement ele = par.get(key);
//            if(ele != null){
//                return  ele.getAsString();
//            }
//        } catch (JsonSyntaxException e) {
//            LogBus.e(TAG,"",e);
//        }
//        return "";
//    }



//    /**
//     * 根据key获取json字符串对应的值
//     * @param json  原json字符串
//     * @param key 键值
//     * @return 返回key对应的值
//     */
//    public static String getJSParameterStr(String json,String key){
//        try {
//            JsonObject jo = gson.fromJson(json,JsonObject.class);
//            JsonObject par = jo.getAsJsonObject(JSKey.JS_PARAMETER);
//            JsonElement ele = par.get(key);
//            if(ele != null && !ele.toString().equals("{}")){
//                return  ele.toString();
//            }
//        } catch (JsonSyntaxException e) {
//            LogBus.e(TAG,"",e);
//        }
//        return "";
//    }

    /**
     *
     * @param json
     * @return
     */
    public static String fromJsonString(String json){
        JsonObject jo = gson.fromJson(json,JsonObject.class);
        return jo.toString();
    }


//    /**
//     * 将对象转换为js脚本，供webiew调用
//     * @param resp
//     * @return
//     */
//    public static String toJsonString(BaseJsonResponse resp){
//        if(resp == null){
//            return "";
//        }
//        JsonObject jsBean = gson.fromJson(gson.toJson(resp),JsonObject.class);
//        JsonObject jo = new JsonObject();
//        jo.addProperty(JSKey.JS_JSNAME,resp.getJsName());
//        jo.addProperty(JSKey.JS_CALLBACKNAME,resp.getCallbackName());
//        jo.add(JSKey.JS_PARAMETER,jsBean);
//        String js = "javascript:"+ JSKey.JS_FUNCTION_NAME+"("+jo.toString()+")";
//        return js;
//    }
//
//    public static List<RightButtonInfoItem> getJSParameterList(String json){
//        List<RightButtonInfoItem> rbi = null;
//        try {
//            rbi = gson.fromJson(json,new TypeToken<List<RightButtonInfoItem>>(){}.getType());
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        return rbi;
//    }

//    public static ArrayList<StatisticsEventInfoItem> getEventInfoItemList(String json){
//        ArrayList<StatisticsEventInfoItem> rbi = gson.fromJson(json,new TypeToken<List<StatisticsEventInfoItem>>(){}.getType());
//        return rbi;
//    }


}
