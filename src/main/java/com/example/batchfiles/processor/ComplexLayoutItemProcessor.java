package com.example.batchfiles.processor;


import com.example.batchfiles.model.source.BaseLayout;
import com.example.batchfiles.model.source.simple.SimpleLayoutDetail;
import com.example.batchfiles.model.source.simple.SimpleLayoutHeader;
import com.example.batchfiles.model.source.simple.SimpleLayoutTrailer;
import com.example.batchfiles.model.target.UniqueLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ComplexLayoutItemProcessor implements ItemProcessor<BaseLayout, UniqueLayout> {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    public UniqueLayout process(BaseLayout item) {
        UniqueLayout transformedLayout = null;
        if(item instanceof SimpleLayoutHeader) {
            //Dados Header
            log.info("Header: {}", item.toString());
        } else if (item instanceof SimpleLayoutDetail) {
            transformedLayout = UniqueLayout.builder().build();
            SimpleLayoutDetail detail = (SimpleLayoutDetail) item;

            transformedLayout.setCodigoTransacao(detail.getRecordCode());
            transformedLayout.setDataLiquidacao(LocalDate.parse(detail.getPaymentDate(), format));
            transformedLayout.setValor(new BigDecimal(((SimpleLayoutDetail) item).getValor()));
            transformedLayout.setDataProcessamento(LocalDate.parse(((SimpleLayoutDetail) item).getProcessDate(), format));
            transformedLayout.setSistemaOrigem("SISTEMA_Y");
            log.info("Detail: {}", item.toString());
            log.info("Converting (" + item + ") into (" + transformedLayout + ")");
        } else if (item instanceof SimpleLayoutTrailer) {
            //Dados Trailer
            log.info("Trailer: {}", item.toString());
        }
        return transformedLayout;
    }

}