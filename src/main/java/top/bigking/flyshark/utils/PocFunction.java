package top.bigking.flyshark.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import top.bigking.flyshark.entity.MyURL;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Random;

@Component
@Slf4j
public class PocFunction {
    // custom function
    public static MyURL newReverse() {
        URL url = PocUtil.generateReverseUrl(8);
        MyURL myURL = new MyURL();
        myURL.setUrl(url);
        return myURL;
    }

    public static String base64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
//        return new BASE64Encoder().encode(str.getBytes());
    }

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
//        return new BASE64Encoder().encode(bytes);
    }

    public static String base64(Byte[] bytes) {
        byte[] bytes1 = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes1[i] = bytes[i];
        }
        return Base64.getEncoder().encodeToString(bytes1);
//        return new BASE64Encoder().encode(bytes);
    }

    public static Byte[] base64Decode(String str) {
        byte[] decode = Base64.getDecoder().decode(str);
        Byte[] bytes = new Byte[decode.length];
        for (int i = 0; i < decode.length; i++) {
            bytes[i] = decode[i];
        }
        return bytes;
    }
    public static String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }
    public static int randomInt(Integer from, Integer to) {
        return new Random().nextInt(to - from + 1) + from;
    }
    public static String randomLowercase(Integer length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(26);
            s.append(str.charAt(number));
        }
        return s.toString();
    }
    public static String randomLowercase(String length) {
        try {
            int len = Integer.parseInt(length);
            return PocFunction.randomLowercase(len);
        } catch (NumberFormatException e) {
            log.error("randomLowercase parse failed!");
            return null;
        }
    }
    public static String urlencode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
    public static String urldecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
    public static Boolean sleep(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    public static String string(byte[] bytes) {
        return new String(bytes);
    }
    public static String string(String str) {
        return str;
    }
    public static String string(Integer i) {
        return String.valueOf(i);
    }
    public static Byte[] bytes(String content) {
        Byte[] result = new Byte[content.length()];
        for (int i = 0; i < content.length(); i++) {
            result[i] = (byte) content.charAt(i);
        }
        return result;
    }
    public static Byte[] bytes(Byte[] b) {
        return b;
    }
    public static Byte[] aesEncrypt(String src, Byte[] key, Byte[] iv) {
        try {
            byte[] keyBytes = new byte[key.length];
            for (int i = 0; i < key.length; i++) {
                keyBytes[i] = key[i];
            }
            byte[] ivBytes = new byte[iv.length];
            for (int i = 0; i < iv.length; i++) {
                ivBytes[i] = iv[i];
            }
            Key skeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivBytes));
            byte[] encrypted = cipher.doFinal(Base64.getDecoder().decode(src));
            Byte[] encryptedBytes = new Byte[encrypted.length];
            for (int i = 0; i < encrypted.length; i++) {
                encryptedBytes[i] = encrypted[i];
            }
            return encryptedBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static String aesEncrypt(String src, Byte[] key, Byte[] iv) {
//        try {
//            byte[] keyBytes = new byte[key.length];
//            for (int i = 0; i < key.length; i++) {
//                keyBytes[i] = key[i];
//            }
//            byte[] ivBytes = new byte[iv.length];
//            for (int i = 0; i < iv.length; i++) {
//                ivBytes[i] = iv[i];
//            }
//            Key skeySpec = new SecretKeySpec(keyBytes, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivBytes));
//            byte[] encrypted = cipher.doFinal(Base64.getDecoder().decode(src));
//            byte[] output = new byte[iv.length + encrypted.length];
//            System.arraycopy(ivBytes, 0, output, 0, ivBytes.length);
//            System.arraycopy(encrypted, 0, output, ivBytes.length, encrypted.length);
//            return Base64.getEncoder().encodeToString(output);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
