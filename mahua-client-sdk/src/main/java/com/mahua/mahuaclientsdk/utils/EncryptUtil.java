package com.mahua.mahuaclientsdk.utils;

import com.mahua.mahuaclientsdk.model.KeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Base64;

/**
 * Ed25519 签名工具类
 */
public class EncryptUtil {

	/**
	 * 生成密钥对，包含公钥和私钥
	 * @return 返回密钥对
	 */
	public static KeyPair getKeys(){
		KeyPair keyPair = new KeyPair();
		Security.addProvider(new BouncyCastleProvider());
		AsymmetricCipherKeyPair generateKeyPair = generateEd25519KeyPair();
		Ed25519PrivateKeyParameters privateKeyParams = (Ed25519PrivateKeyParameters) generateKeyPair.getPrivate();
		Ed25519PublicKeyParameters publicKeyParams = (Ed25519PublicKeyParameters) generateKeyPair.getPublic();
		byte[] privateKeyBytes = privateKeyParams.getEncoded();
		byte[] publicKeyBytes = publicKeyParams.getEncoded();
		// Base64 编码将 byte 数组转换成字符串，方便存储在数据库中
		keyPair.setPrivateKey(Base64.getEncoder().encodeToString(privateKeyBytes));
		keyPair.setPublicKey(Base64.getEncoder().encodeToString(publicKeyBytes));
		return keyPair;
	}

	/**
	 * 对请求数据使用私钥钥进行签名
	 * @param param 请求数据
	 * @param privateKey 密钥
	 * @return
	 */
	public static String getSign(String param, String privateKey) {
		byte[] retrievedPrivateKeyBytes = Base64.getDecoder().decode(privateKey);
		Ed25519PrivateKeyParameters retrievedPrivateKeyParams = new Ed25519PrivateKeyParameters(retrievedPrivateKeyBytes, 0);
		// 要签名的数据(请求参数)
		byte[] message = param.getBytes();
		Ed25519Signer signer = new Ed25519Signer();
		signer.init(true, retrievedPrivateKeyParams);
		signer.update(message, 0, message.length);
		byte[] bytes = signer.generateSignature();
		String sign = new String(bytes);
		return sign;
	}

	/**
	 * 对请求参数进行验签
	 * @param publicKey 公钥
	 * @param sign 需要验证的签名
	 * @param param 需要验证的请求参数
	 * @return
	 */
	public static boolean verifySign(String publicKey, String sign, String param){
		byte[] retrievedPublicKeyBytes = Base64.getDecoder().decode(publicKey);
		// 反序列化为Ed25519PublicKeyParameters对象
		Ed25519PublicKeyParameters retrievedPublicKeyParams = new Ed25519PublicKeyParameters(retrievedPublicKeyBytes, 0);

		// 使用私钥对数据进行签名
		byte[] signature = Base64.getDecoder().decode(sign);
		Ed25519Signer verifier = new Ed25519Signer();
		verifier.init(false, retrievedPublicKeyParams);
		// 要签名的数据(请求参数)
		byte[] message = param.getBytes();
		verifier.update(message, 0, message.length);
		return verifier.verifySignature(signature);
	}


	private static AsymmetricCipherKeyPair generateEd25519KeyPair() {
		Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
		generator.init(new Ed25519KeyGenerationParameters(null));
		return generator.generateKeyPair();
	}
}