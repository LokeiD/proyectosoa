package com.coopac.sistemasoa.dto;
import com.coopac.sistemasoa.expediente.model.Periodo;
import com.coopac.sistemasoa.expediente.model.ProductoCrediticio;
import com.coopac.sistemasoa.expediente.model.Recurrencia;
import com.coopac.sistemasoa.expediente.model.Riesgo;
import lombok.Data;
import java.util.List;

@Data
public class CargaComboBoxFormularioDTO {
    private List<ProductoCrediticio> productos;
    private List<Recurrencia> recurrencias;
    private List<Riesgo> riesgos;
    private List<Periodo> periodos;
}
