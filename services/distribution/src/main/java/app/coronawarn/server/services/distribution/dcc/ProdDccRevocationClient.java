package app.coronawarn.server.services.distribution.dcc;

import app.coronawarn.server.common.persistence.domain.RevocationEntry;
import app.coronawarn.server.services.distribution.dcc.decode.DccRevocationListDecodeException;
import app.coronawarn.server.services.distribution.dcc.decode.DccRevocationListDecoder;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!fake-dcc-revocation & revocation")
public class ProdDccRevocationClient implements DccRevocationClient {

  private static final Logger logger = LoggerFactory.getLogger(ProdDccRevocationClient.class);

  private final DccRevocationFeignClient dccRevocationFeignClient;
  private final DccRevocationListDecoder dccRevocationListDecoder;

  public ProdDccRevocationClient(DccRevocationFeignClient dccRevocationFeignClient,
      DccRevocationListDecoder dccRevocationListDecoder) {
    this.dccRevocationFeignClient = dccRevocationFeignClient;
    this.dccRevocationListDecoder = dccRevocationListDecoder;
  }

  @Override
  public Optional<List<RevocationEntry>> getDccRevocationList() throws FetchDccListException {
    logger.debug("Get Revocation List from DCC");
    try {
      return Optional.of(dccRevocationListDecoder.decode(dccRevocationFeignClient.getRevocationList().getBody()));
    } catch (DccRevocationListDecodeException e) {
      logger.error("DCC Revocation List could not be decoded.", e);
    } catch (Exception e) {
      throw new FetchDccListException("DCC Revocation List could not be fetched because of: ", e);
    }
    return Optional.empty();
  }
}