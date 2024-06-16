/*
 * Created 2018-8-27 11:09:10
 */
package cn.com.yting.kxy.web.ethereum;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

/**
 *
 * @author Azige
 */
@Component
public class CredentialsHolder {

    private static final String walletPassphrase = "de91b526fc2b8d69b56a5836c7e92bbb1cea1498";

    private final Credentials credentials;

    public CredentialsHolder() throws IOException, CipherException {
        try (InputStream input = CredentialsHolder.class.getClassLoader().getResourceAsStream("kxy-server-wallet.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            WalletFile walletFile = objectMapper.readValue(input, WalletFile.class);
            ECKeyPair keyPair = Wallet.decrypt(walletPassphrase, walletFile);
            credentials = Credentials.create(keyPair);
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
