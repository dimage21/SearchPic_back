package dimage.searchpic.util;

import dimage.searchpic.domain.location.Location;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitPlaceData {
    @Value("${file.upload-dir}")
    private String baseDir;
    private final InitService initService;
    private final ExcelUtil excelUtil;

    @PostConstruct
    public void saveData(){
        try (InputStream fileInputStream = new FileInputStream(baseDir + "place.xlsx")) {
            List<Location> locations = excelUtil.getLocations(fileInputStream, 1, 8);
            for (Location location : locations) {
                initService.saveLocation(location);
            }
        } catch (IOException e){e.printStackTrace();}
    }
    @Component
    static class InitService {
        @Autowired private EntityManager em;
        @Transactional
        public void saveLocation(Location location) {
            em.persist(location);
        }
    }

	@Component
	static class ExcelUtil {
        private String getCellValue(XSSFCell cell){
            String value = "";
            if (cell == null)
                return value;
            try {
                value = cell.getStringCellValue();
            } catch(IllegalStateException e) {
                value = Double.toString(cell.getNumericCellValue());
            }
            return value;
        }

        public List<Location> getLocations(InputStream stream, int rowStart, int colLength) {
            List<Location> locations = new ArrayList<>();
            XSSFCell cell;
            XSSFRow row;
            int rowIndex, colIndex;
            Location location;
            List<String> values = new ArrayList<>();
            try {
                OPCPackage opcPackage = OPCPackage.open(stream);
                @SuppressWarnings("resource")
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
                XSSFSheet sheet = workbook.getSheetAt(0); // 첫번째 시트
                for (rowIndex = rowStart; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // 컬럼명은 제외하고 진행
                    values.clear();
                    row = sheet.getRow(rowIndex);
                    for (colIndex = 0; colIndex < colLength; colIndex++) {
                        cell = row.getCell(colIndex);
                        String value = getCellValue(cell);
                        values.add(value);
                    }
                    location = new Location(Double.parseDouble(values.get(0)), Double.parseDouble(values.get(1)),
                                            values.get(2), values.get(3), values.get(4),
                                            values.get(5), values.get(6), null, values.get(7));
                    locations.add(location);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return locations;
        }
    }
}