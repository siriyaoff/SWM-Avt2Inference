package com.avatar2.inference.web.dto;

import com.avatar2.inference.domain.inference.PElem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
public class InferenceResponseDto {
    private ArrayList<PElem> coord = new ArrayList<>();
    private ArrayList<PElem> eyelash = new ArrayList<>();
    private MultipartFile iris;

    public InferenceResponseDto(ArrayList<PElem> coord, ArrayList<PElem> eyelash, MultipartFile iris) {
        this.coord = new ArrayList<>();
        for (PElem p : coord) {
            this.coord.add(new PElem(p.getId(), p.getX(), p.getY()));
        }
        this.eyelash = new ArrayList<>();
        for (PElem p : eyelash) {
            this.eyelash.add(new PElem(p.getId(), p.getX(), p.getY()));
        }
        this.iris = iris;
    }
}
