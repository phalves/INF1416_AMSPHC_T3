package controller;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import model.authentication.User;

public class FileChecker {

	public String checkFile(String fileName, String caminhoPasta, User user) {

		byte[] encryptedFileBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".enc");
		byte[] envelopeBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".env");
		byte[] digitalSignatureBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".asd");

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, user.getPrivateKey());
			byte[] newPlainText = cipher.doFinal(envelopeBytes);

			//newplainText é a seed usada pra gerar chave simetrica no momento da criptografia do .enc
			//gera de novo a chave simetrica
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56, new SecureRandom(newPlainText));
			Key encryptedFileKey = keyGen.generateKey();

			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, encryptedFileKey);
			byte[] IndexBytes = cipher.doFinal(encryptedFileBytes);

			//assinando o conteudo do arquivo .enc ja decriptado
			Signature signature = Signature.getInstance("MD5WithRSA");
			signature.initSign(user.getPrivateKey());
			signature.update(IndexBytes);

			byte[] signedBytes = signature.sign();

			if (signedBytes.length == digitalSignatureBytes.length) {
				boolean authenticFile = true;

				for (int i = 0; i < signedBytes.length; i++) 
				{
					if (signedBytes[i] != digitalSignatureBytes[i]) {
						authenticFile = false;
					}
				}
				return authenticFile ? "OK" : "NOT OK";
			} else {
				return "NOT OK";
			}
		} catch (Exception e) {
			return "NOT OK";
		}
	}
}

