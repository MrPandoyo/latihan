package com.artivisi.kampus.latihan.service;

import com.artivisi.kampus.latihan.dao.RuanganDao;
import com.artivisi.kampus.latihan.entity.Ruangan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStreamReader;

@Service
public class RuanganCsvService {

    @Autowired
    private RuanganDao ruanganDao;

    private CellProcessor[] mappingHeaderRuangan() {

        final CellProcessor[] processors = new CellProcessor[] {
                new UniqueHashCode(), // kode_ruangan
        };

        return processors;
    }

    public void mappingDataRuangan(MultipartFile file) throws Exception {

        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new InputStreamReader(file.getInputStream()), CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true) ;
            final CellProcessor[] processors = mappingHeaderRuangan();

            Ruangan ruangan;
            while( (ruangan = beanReader.read(Ruangan.class, header, processors)) != null ) {
                System.out.println(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(),
                        beanReader.getRowNumber(), ruangan));

                Ruangan newRuangan = new Ruangan();
                newRuangan.setKodeRuangan(ruangan.getKodeRuangan());
                ruanganDao.save(newRuangan);
            }

        }
        finally {
            if( beanReader != null ) {
                beanReader.close();
            }
        }
    }

}
