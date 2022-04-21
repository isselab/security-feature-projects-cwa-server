package app.coronawarn.server.services.distribution.dcc.decode;

import static app.coronawarn.server.common.shared.util.SerializationUtils.jsonExtractCosePayload;

import app.coronawarn.server.common.persistence.domain.RevocationEntry;
import app.coronawarn.server.services.distribution.dgc.exception.DscListDecodeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DccRevocationListDecoder {

  public DccRevocationListDecoder() {
  }

  /**
   * Decode the trust list of certificates. Verifies the trust list content by using the ECDSA signature logic. Filters
   * only X509 valid format certificates from the response.
   *
   * @param data - trust list response from DSC as string.
   * @return - object wrapping the list of certificates.
   * @throws DscListDecodeException - thrown if any exception is caught and special treatment if signature verification
   *                                fails.
   */
  public List<RevocationEntry> decode(byte[] data) throws DccRevocationListDecodeException {
    ArrayList<RevocationEntry> revocationEntries = new ArrayList<>();
    try {
      Map<byte[], List<byte[]>> jsonPayload = jsonExtractCosePayload(data);

      jsonPayload.forEach((keyAndType, values) -> {
        byte[] kid = Arrays.copyOfRange(keyAndType, 0, keyAndType.length - 1);
        byte[] type = Arrays.copyOfRange(keyAndType, keyAndType.length - 1, keyAndType.length);

        values.forEach(hash ->
            revocationEntries.add(new RevocationEntry(kid, type, hash)));
      });
    } catch (Exception e) {
      throw new DccRevocationListDecodeException("DCC revocation list NOT decoded.", e);
    }
    return revocationEntries;
  }
}