package com.coopac.sistemasoa.dto;
import com.coopac.sistemasoa.model.*;
import lombok.Data;
import java.util.List;

@Data
public class CargaComboBoxFormularioDTO {
    private List<ProductoCrediticio> productos;
    private List<Recurrencia> recurrencias;
    private List<Riesgo> riesgos;
    private List<Periodo> periodos;
}
