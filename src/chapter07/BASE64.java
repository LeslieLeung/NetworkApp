package chapter07;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/19
 */
public class BASE64 {
    public static void main(String[] args) {
        String userName="429242349@qq.com";
        String authCode = "yvhdqefhaquxbiee";
        //显示邮箱名的base64编码结果
        System.out.println(encode(userName));
        //显示授权码的base64编码结果
        System.out.println(encode(authCode));
    }

    public static String encode(String str) {
        return new sun.misc.BASE64Encoder().encode(str.getBytes());
    }

}
