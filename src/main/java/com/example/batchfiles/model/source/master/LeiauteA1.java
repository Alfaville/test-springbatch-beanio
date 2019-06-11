package com.example.batchfiles.model.source.master;

import com.example.batchfiles.model.source.LeiauteBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;

import java.util.ArrayList;
import java.util.List;

@Record(minOccurs = 1, maxOccurs = 1)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LeiauteA1 extends LeiauteBase {

    @Segment
    private LeiauteHeader header;

    @Segment
    private List<LeiauteDetail> details = new ArrayList<>();

    @Segment
    private LeiauteTrailer trailer;

}