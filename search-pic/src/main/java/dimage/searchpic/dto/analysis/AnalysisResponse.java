package dimage.searchpic.dto.analysis;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AnalysisResponse {
    private ArrayList<String> data;
    private boolean success;
}
