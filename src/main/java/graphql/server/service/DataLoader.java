package graphql.server.service;

import com.google.common.base.Splitter;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import graphql.server.model.*;
import graphql.server.repository.ElementConversionControlsRepository;
import graphql.server.repository.IntegratorControlsRepository;
import graphql.server.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class DataLoader {

    @Autowired private SatelliteRepository satelliteRepository;
    @Autowired private IntegratorControlsRepository integratorControlsRepository;
    @Autowired private ElementConversionControlsRepository elementConversionControlsRepository;

    public void loadDataIntoHSQL() {
        log.debug(("Loading data into the embedded test database..."));

        log.debug("    Loading Satellite data...");
        // Load Satellite Data
        URL satelliteDataURL = Resources.getResource("data/satellite.csv");
        // Load IntegratorControls Data
        URL icURL = Resources.getResource("data/integratorControls.csv");
        URL eccURL = Resources.getResource("data/elementConversionControls.csv");
        BufferedReader reader = null;
        try {
            CharSource charSource = Resources.asCharSource(satelliteDataURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToSatellite).forEach(satellite -> {
                log.debug("        Saving Satellite {}", satellite.getId());
                satelliteRepository.save(satellite);
            });
            reader.close();

            log.debug("    Loading IntegratorControls data...");
            charSource = Resources.asCharSource(icURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToIntegratorControls).forEach(ic -> {
                log.debug("        Saving IntegratorControls {}", ic.getId());
                integratorControlsRepository.save(ic);
            });

            log.debug("    Loading ElementConversionControls data...");
            charSource = Resources.asCharSource(eccURL, UTF_8);
            reader = charSource.openBufferedStream();
            reader.lines().map(mapToElementConversionControls).forEach(ecc -> {
                log.debug("        Saving ElementConversionControls {}", ecc.getId());
                elementConversionControlsRepository.save(ecc);
            });

        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error closing stream.", e);
                }
            }
        }
    }

    private Function<String, Satellite> mapToSatellite = (line) -> {
        List<String> satFields = Splitter.on(',').trimResults().splitToList(line);
        try {
            Satellite sat = new Satellite(satFields.get(0), Integer.parseInt(satFields.get(1)), satFields.get(2), satFields.get(3), Integer.parseInt(satFields.get(4)));
            return sat;
        } catch (NumberFormatException e) {
            log.error("Error converting csv line to Satellite.", e);
        }
        return null;
    };

    private Function<String, IntegratorControls> mapToIntegratorControls = (line) -> {
        List<String> fields = Splitter.on(',').trimResults().splitToList(line);
        try {
            IntegratorControls ic = new IntegratorControls(
                    fields.get(0),
                    fields.get(1),
                    ApplicationEnum.getByIntValue(Integer.valueOf(fields.get(2))),
                    IcCoordinateSystem.getByIntValue(Integer.valueOf(fields.get(3))),
                    Double.valueOf(fields.get(4)),
                    Double.valueOf(fields.get(5)),
                    PartialDerivatives.getByIntValue(Integer.valueOf(fields.get(6))),
                    Boolean.valueOf(fields.get(7)),
                    Propagator.getByIntValue(Integer.valueOf(fields.get(8))),
                    Boolean.valueOf(fields.get(9)),
                    StepMode.getByIntValue(Integer.valueOf(fields.get(10))),
                    StepSizeMethod.getByIntValue(Integer.valueOf(fields.get(11))),
                    StepSizeSource.getByIntValue(Integer.valueOf(fields.get(12)))
            );
            return ic;
        } catch (NumberFormatException e) {
            log.error("Error converting csv line to IntegratorControls.", e);
        }
        return null;
    };

    private Function<String, ElementConversionControls> mapToElementConversionControls = (line) -> {
        List<String> fields = Splitter.on(',').trimResults().splitToList(line);
        try {
            ElementConversionControls ecc = new ElementConversionControls(
                    fields.get(0),
                    fields.get(1),
                    ApplicationEnum.getByIntValue(Integer.valueOf(fields.get(2))),
                    EpochPlacement.getByIntValue(Integer.valueOf(fields.get(3))),
                    Double.valueOf(fields.get(4)),
                    Double.valueOf(fields.get(5)),
                    Boolean.valueOf(fields.get(6)),
                    Double.valueOf(fields.get(7)),
                    Double.valueOf(fields.get(8)),
                    Integer.valueOf(fields.get(9)),
                    Double.valueOf(fields.get(10)),
                    Double.valueOf(fields.get(11)),
                    Double.valueOf(fields.get(12)),
                    ExtrapolationSpanUnits.getByIntValue(Integer.valueOf(fields.get(13)))
            );
            return ecc;
        } catch (NumberFormatException e) {
            log.error("Error converting csv line to IntegratorControls.", e);
        }
        return null;
    };
}
