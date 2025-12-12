
package com.coopac.sistemasoa.expediente.service.integration;

import com.coopac.sistemasoa.expediente.model.dto.DocumentoExpedienteDTO;

import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.repository.ExpedienteCreditoRepository;
import com.coopac.sistemasoa.expediente.service.support.DocumentoSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CentralRiesgosService {

    @Autowired
    private DocumentoSupportService documentoSupportService;

    @Autowired
    private ExpedienteCreditoRepository expedienteRepository;

    @Value("${pdfshift.api.key}")
    private String apiKey;

    private final String API_PDFSHIFT = "https://api.pdfshift.io/v3/convert/pdf";

    public DocumentoExpedienteDTO consultarYGuardarHistorial(String dni, Integer codExpediente) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println(">>> [SOA] Generando Reporte con Diseño Personalizado para DNI: " + dni);

            // 1. OBTENER DATOS REALES DEL SOCIO
            ExpedienteCredito expediente = expedienteRepository.findById(codExpediente)
                    .orElseThrow(() -> new SoaException("404", "Expediente no encontrado para generar reporte."));

            // Construimos el nombre completo
            String nombreCompleto = "";
            if (expediente.getSocio() != null) {
                nombreCompleto = expediente.getSocio().getNombres() + " " +
                        expediente.getSocio().getApellidoPaterno() + " " +
                        expediente.getSocio().getApellidoMaterno();
            } else {
                nombreCompleto = "SOCIO NO IDENTIFICADO";
            }
            nombreCompleto = nombreCompleto.toUpperCase();

            // 1.1 GENERAR OTROS DATOS DINÁMICOS (Score, Fecha, etc.)
            int score = new Random().nextInt(551) + 300; // 300 - 850
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Lógica de colores y textos según el score
            String colorScore = score > 650 ? "#22c55e" : (score > 500 ? "#eab308" : "#ef4444"); // Verde, Amarillo, Rojo
            String textoRiesgo = score > 650 ? "RIESGO BAJO" : (score > 500 ? "RIESGO MEDIO" : "RIESGO ALTO");

            // 2. HTML ADAPTADO (Java Text Block)
            // Se han duplicado los '%' de CSS (%%) para que Java no falle.
            // Se usan placeholders (%s, %d) mapeados a los argumentos del .formatted().
            // Se usan índices de argumentos (ej. %2$s) para reutilizar datos en la sección Consulta Rápida.
            String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reporte de Crédito - Experian</title>
                    <script src="https://cdn.tailwindcss.com"></script>
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=Arimo:ital,wght@0,400;0,700;1,400;1,700&display=swap');
                        
                        body {
                            font-family: 'Arimo', Arial, sans-serif;
                            background-color: white;
                            color: #000;
                        }
                
                        /* Bordes negros gruesos característicos de la sección superior */
                        .thick-border-y {
                            border-top: 4px solid black;
                            border-bottom: 4px solid black;
                        }
                
                        /* Ajustes finos para la tabla */
                        table { border-collapse: collapse; }
                        th { background-color: #e5e7eb; font-weight: 700; }
                        
                        /* Iconos personalizados */
                        .icon-check { color: #808080; } 
                        .icon-empty { color: #ccc; } 
                
                        /* Clases específicas para la nueva sección Sabio */
                        .sabio-header {
                            background-color: #2b4e8f; /* Azul oscuro corporativo */
                        }
                        .bg-success-circle {
                            background-color: %1$s; /* COLOR DINÁMICO (Arg 1) */
                        }
                        .text-teal-dark {
                            color: #206b78; /* Color verde azulado de los títulos */
                        }
                        
                        /* Barra gris degradada simulada para Consulta Rápida */
                        .header-gray-gradient {
                            background: linear-gradient(to bottom, #4a4a4a 0%%, #333333 100%%); /* %% ESCAPADO */
                            box-shadow: inset 0 1px 0 rgba(255,255,255,0.2);
                        }
                    </style>
                </head>
                <body class="p-4 bg-white min-h-screen flex justify-center">
                
                    <div class="w-full max-w-[1000px]">
                        
                        <!-- ========================================== -->
                        <!--        SECCIÓN 1: REPORTE DE CRÉDITO       -->
                        <!-- ========================================== -->
                
                        <!-- 1. BARRA SUPERIOR DE INFORMACIÓN -->
                        <div class="flex justify-between items-end text-xs mb-1 font-bold">
                            <div class="flex gap-12">
                                <span class="text-sm">DNI</span>
                                <span class="text-sm">%2$s</span> <!-- DNI (Arg 2) -->
                            </div>
                            <div class="flex-grow text-center text-gray-500 font-normal ml-12">
                                Información actualizada al:
                            </div>
                            <div class="text-[11px]">
                                Fecha y Hora de creación: <span class="font-normal">%3$s</span> <!-- Fecha (Arg 3) -->
                            </div>
                        </div>
                
                        <!-- 2. CABECERA CON LOGO Y TÍTULO -->
                        <div class="thick-border-y py-3 flex items-center justify-between mb-6">
                            <!-- Logo Simulado -->
                            <div class="flex items-center w-1/3">
                                <div class="relative w-10 h-10 mr-2 flex-shrink-0">
                                    <div class="absolute top-0 left-0 w-3 h-3 bg-[#00a3e0] rounded-[1px]"></div>
                                    <div class="absolute top-4 left-4 w-3 h-3 bg-[#00558f] rounded-[1px]"></div>
                                    <div class="absolute top-4 left-0 w-3 h-3 bg-[#e6007e] rounded-[1px]"></div>
                                    <div class="absolute top-0 left-4 w-3 h-3 bg-[#89cff0] opacity-50 rounded-[1px]"></div>
                                    <div class="absolute top-6 left-2 w-1.5 h-1.5 bg-[#ffb6c1] opacity-60 rounded-[1px]"></div>
                                </div>
                                <div>
                                    <span class="text-4xl font-bold text-[#00558f] tracking-tighter leading-none">experian</span>
                                    <span class="text-[9px] text-[#00558f] align-top font-bold">TM</span>
                                </div>
                            </div>
                
                            <!-- Título Centrado -->
                            <div class="w-1/3 text-center">
                                <h1 class="text-3xl font-bold text-black tracking-tight">Reporte de Crédito</h1>
                            </div>
                            
                            <div class="w-1/3"></div>
                        </div>
                
                        <!-- 3. SECCIÓN DE CHECKBOXES -->
                        <div class="border border-gray-500 p-3 mb-6">
                            <div class="flex items-center gap-2 mb-3 text-xs pl-1">
                                <span>El reporte contiene solo los títulos seleccionados</span>
                                <i class="fas fa-check-circle icon-check text-lg"></i>
                            </div>
                
                            <div class="grid grid-cols-6 gap-y-2 gap-x-1 text-[11px] items-center">
                                <!-- Fila 1 -->
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Información General</div>
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Consulta Rápida</div>
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Principales Acreedores</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Detalle Variación</div>
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Posición Histórica</div>
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Gráficos</div>
                
                                <!-- Fila 2 -->
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Rep.Vencidos</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Rep. SBS/Microf.</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> SBS/Microf. Entidades</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Rectificaciones SBS</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Avalados/Avalistas</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Hechos de Importancia</div>
                
                                <!-- Fila 3 -->
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Documentos CCL</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Detalle de Vencidos</div>
                                <div class="flex items-center gap-2"><i class="fas fa-check-circle icon-check text-xl"></i> Comercio Exterior</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Otros Créditos</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Otros Créd. Entidades</div>
                                <div class="flex items-center gap-2"><i class="fas fa-circle icon-empty text-xl"></i> Discrepancias</div>
                            </div>
                        </div>
                
                        <!-- 4. CONTENIDO PRINCIPAL (TABLA + SCORE PEQUEÑO) -->
                        <div class="flex border-t-4 border-black mt-2 pt-1 gap-2 mb-12">
                            
                            <!-- Columna Izquierda: Tabla -->
                            <div class="flex-grow w-3/4">
                                <div class="bg-gray-100 py-1.5 px-2 mb-4 border-l-4 border-gray-100">
                                    <h2 class="font-bold italic text-gray-800 text-sm tracking-wide">
                                        DNI %4$s - %5$s <!-- DNI (Arg 4), Nombre (Arg 5) -->
                                    </h2>
                                </div>
                
                                <table class="w-full text-[11px] border-collapse">
                                    <thead>
                                        <tr class="bg-gray-200 border-b border-white h-8">
                                            <th class="pl-2 text-left w-24">Fecha Proceso</th>
                                            <th class="pl-2 text-left w-16">Tipo Doc.</th>
                                            <th class="pl-2 text-left w-24">N. Doc.</th>
                                            <th class="text-center w-20">Semáforo</th>
                                            <th class="text-right pr-4 w-24 relative">
                                                Deuda Total <span class="absolute top-1 right-2">*</span>
                                            </th>
                                            <th class="text-center w-8">Var.</th>
                                            <th class="text-center w-8">Actual</th>
                                            <th class="text-center w-8">Previo</th>
                                            <th class="text-center w-8">12m</th>
                                        </tr>
                                    </thead>
                                    <tbody class="text-xs">
                                        <tr class="border-b border-gray-200 h-10">
                                            <td class="pl-2 font-normal">%6$s</td> <!-- Fecha Corta (Arg 6) -->
                                            <td class="pl-2">DNI</td>
                                            <td class="pl-2">%7$s</td> <!-- DNI (Arg 7) -->
                                            <td class="text-center">0.000</td>
                                            <td class="text-right pr-4">0.00</td>
                                            <td class="text-center"><i class="far fa-file-alt text-gray-500 text-sm"></i></td>
                                            <td class="text-center"><div class="w-3 h-3 bg-gray-300 rounded-full border border-gray-400 mx-auto"></div></td>
                                            <td class="text-center"><div class="w-3 h-3 bg-gray-300 rounded-full border border-gray-400 mx-auto"></div></td>
                                            <td class="text-center relative h-10 w-8 align-middle">
                                                <div class="w-4 h-4 rounded-full border border-gray-400 mx-auto overflow-hidden relative bg-gray-100">
                                                    <div class="absolute right-0 top-0 w-2 h-4 bg-green-600"></div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr class="h-10">
                                            <td class="pl-2 font-normal">10/12/2025</td>
                                            <td class="pl-2">RUC</td>
                                            <td class="pl-2">10736534792</td>
                                            <td class="text-center">0.000</td>
                                            <td class="text-right pr-4">0.00</td>
                                            <td class="text-center"></td>
                                            <td class="text-center"><div class="w-3 h-3 bg-gray-200 rounded-full border border-gray-400 mx-auto"></div></td>
                                            <td class="text-center"><div class="w-3 h-3 border border-gray-400 mx-auto"></div></td>
                                            <td class="text-center"><div class="w-3 h-3 border border-gray-400 mx-auto"></div></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                
                            <!-- Columna Derecha: Termómetro Score (Pequeño) -->
                            <div class="w-px bg-gray-300 mx-2 self-stretch"></div>
                            <div class="w-1/4 flex flex-col items-center pt-1">
                                <div class="text-center leading-tight mb-2">
                                    <div class="text-[11px] text-black">Score</div>
                                    <div class="text-[11px] text-black">Experian</div>
                                </div>
                                <div class="relative">
                                    <svg width="50" height="130" viewBox="0 0 50 130">
                                        <path d="M18,10 L32,10 L32,100 A12,12 0 1,1 18,100 Z" fill="none" stroke="#666" stroke-width="1" />
                                        <path d="M20,35 L30,35 L30,100 A10,10 0 1,1 20,100 Z" fill="%8$s" /> <!-- Color (Arg 8) -->
                                        <line x1="20" y1="35" x2="30" y2="35" stroke="#666" stroke-width="1" />
                                        <line x1="23" y1="50" x2="27" y2="50" stroke="#999" stroke-width="1" />
                                        <line x1="23" y1="65" x2="27" y2="65" stroke="#999" stroke-width="1" />
                                        <line x1="23" y1="80" x2="27" y2="80" stroke="#999" stroke-width="1" />
                                        <path d="M22,12 L28,12 A2,2 0 0,1 28,16 L22,16 A2,2 0 0,1 22,12" fill="none" stroke="#16a34a" stroke-width="1.5" />
                                    </svg>
                                    <div class="absolute top-[40px] right-[-25px] text-sm font-bold text-black">%9$d</div> <!-- Score (Arg 9) -->
                                </div>
                                <div class="text-center font-bold text-[10px] mt-1">Puntaje Medio</div>
                            </div>
                        </div>
                
                        <!-- ========================================== -->
                        <!--        SECCIÓN 2: SABIO EMPRESARIAL        -->
                        <!-- ========================================== -->
                
                        <div class="w-full mb-16">
                            <!-- Barra Negra de Título -->
                            <div class="bg-[#333] text-white font-bold py-2 text-center text-sm shadow-md rounded-sm">
                                Sabio Empresarial
                            </div>
                
                            <!-- Indicador Bancarizado -->
                            <div class="flex justify-end items-center gap-2 mt-3 mb-2 text-sm font-bold text-gray-700">
                                <i class="fas fa-university text-gray-500"></i>
                                <span>Bancarizado: NO</span>
                            </div>
                
                            <!-- Contenedor Principal en 2 Columnas -->
                            <div class="flex gap-4 items-stretch">
                                
                                <!-- COLUMNA IZQUIERDA -->
                                <div class="w-1/2 flex flex-col gap-4">
                                    
                                    <!-- Tarjeta 1: Resultado de Sabio -->
                                    <div class="border border-gray-400 bg-white shadow-sm">
                                        <!-- Cabecera Azul -->
                                        <div class="sabio-header text-white p-3">
                                            <h3 class="font-bold text-md leading-none">Resultado de Sabio</h3>
                                            <p class="text-[10px] leading-tight mt-1 opacity-90 font-light">
                                                Para determinar objetivamente el nivel de cumplimiento se han utilizado reglas estándar asociadas al comportamiento de pago del titular.
                                            </p>
                                        </div>
                                        
                                        <!-- Cuerpo con Icono Grande -->
                                        <div class="p-6 flex items-center justify-center gap-6">
                                            <!-- Círculo Verde con Pulgar -->
                                            <div class="w-24 h-24 bg-success-circle rounded-full flex items-center justify-center text-white text-5xl shadow-sm">
                                                <i class="fas fa-thumbs-up transform -rotate-6 pb-1"></i>
                                            </div>
                                            
                                            <!-- Texto -->
                                            <div>
                                                <div class="text-2xl font-bold text-gray-700 leading-none">%10$s</div> <!-- Texto Riesgo (Arg 10) -->
                                                <div class="text-gray-600 text-sm mt-1">Producto Genérico</div>
                                            </div>
                                        </div>
                
                                        <!-- Footer de la tarjeta -->
                                        <div class="px-4 pb-4">
                                            <div class="border-t border-gray-300 pt-2 text-[11px] text-black">
                                                <span class="font-bold">Detalle:</span>
                                                <div class="mt-0.5">- Cumple con todas las reglas de evaluación</div>
                                            </div>
                                        </div>
                                    </div>
                
                                    <!-- Tarjeta 2: Capacidad de Pago -->
                                    <div class="border border-gray-400 p-5 flex justify-between items-center bg-white shadow-sm h-full">
                                        <div>
                                            <h3 class="font-bold text-teal-dark text-sm">Capacidad de Pago Mensual</h3>
                                            <p class="text-[11px] text-gray-600 mt-2 mb-2">Según el cumplimiento de deudas reportadas</p>
                                            <div class="font-bold text-gray-700 text-lg">S/ 510 a S/ 600</div>
                                        </div>
                                        <div class="text-green-400 text-5xl opacity-90">
                                            <!-- Icono de mano con billetes simulado con FA -->
                                            <div class="relative">
                                                <i class="fas fa-hand-holding-dollar text-[#3b5c64] transform rotate-12 scale-x-[-1]"></i>
                                                <i class="fas fa-money-bill-wave absolute -top-4 -right-2 text-green-400 text-3xl transform -rotate-12 z-10"></i>
                                            </div>
                                        </div>
                                    </div>
                
                                </div>
                
                                <!-- COLUMNA DERECHA: Score Experian (Grande) -->
                                <div class="w-1/2 border border-gray-400 p-5 relative bg-white shadow-sm flex flex-col">
                                    <h3 class="font-bold text-teal-dark text-md mb-8">Score Experian</h3>
                                    
                                    <div class="flex-grow flex justify-center items-center gap-10 mb-8 px-8">
                                        <span class="text-sm text-black">Puntaje Medio</span>
                                        
                                        <!-- Termómetro Grande -->
                                        <div class="relative">
                                            <svg width="60" height="150" viewBox="0 0 60 150">
                                                <!-- Tubo y Bulbo -->
                                                <path d="M20,10 L40,10 L40,110 A16,16 0 1,1 20,110 Z" fill="white" stroke="#555" stroke-width="1.2" />
                                                
                                                <!-- Nivel Líquido Amarillo -->
                                                <path d="M22,40 L38,40 L38,110 A14,14 0 1,1 22,110 Z" fill="%11$s" /> <!-- Color (Arg 11) -->
                                                
                                                <!-- Línea superior del líquido -->
                                                <path d="M22,40 L38,40" stroke="#555" stroke-width="1" />
                                                
                                                <!-- Tubo interior verde (marcador) -->
                                                <path d="M26,14 L34,14 A3,3 0 0,1 34,20 L26,20 A3,3 0 0,1 26,14" fill="none" stroke="#22c55e" stroke-width="1.5" />
                                                <line x1="30" y1="20" x2="30" y2="40" stroke="#22c55e" stroke-width="1.5" />
                
                                            </svg>
                                            <div class="absolute top-[40px] right-[-35px] text-sm font-bold text-black">%12$d</div> <!-- Score (Arg 12) -->
                                        </div>
                                    </div>
                
                                    <!-- Texto explicativo inferior -->
                                    <div class="border-t border-gray-300 pt-6 text-center text-xs text-black w-4/5 mx-auto leading-relaxed">
                                        En este nivel de riesgo, 19 de cada 100 personas podrían caer en incumplimientos en los próximos 12 meses
                                    </div>
                                </div>
                
                            </div>
                        </div>
                
                        <!-- ========================================== -->
                        <!--        SECCIÓN 3: CONSULTA RÁPIDA          -->
                        <!-- ========================================== -->
                
                        <div class="w-full">
                            <!-- Barra Gris Oscura Título -->
                            <div class="header-gray-gradient text-white text-center py-2.5 shadow-md rounded-sm mb-6">
                                <h2 class="text-lg font-bold tracking-wide">Consulta Rápida</h2>
                            </div>
                
                            <!-- Título de Sección -->
                            <h3 class="font-bold text-black text-sm mb-1">
                                Documento de identidad consultado
                            </h3>
                            <div class="border-b-[3px] border-black w-full mb-4"></div>
                
                            <!-- Tabla de Consulta Rápida -->
                            <table class="w-full text-[10px] mb-12">
                                <thead>
                                    <tr class="bg-gray-200 text-black h-8">
                                        <th class="pl-2 text-left font-bold w-20">Fecha Proceso</th>
                                        <th class="pl-2 text-left font-bold w-12">Tipo Doc.</th>
                                        <th class="pl-2 text-left font-bold w-20">N. Doc.</th>
                                        <th class="pl-2 text-left font-bold">Nombre o Raz. Social</th>
                                        <th class="text-center font-bold w-16">Semáforo</th>
                                        <th class="text-right font-bold w-20 pr-4 relative">
                                            Deuda Total <span class="absolute top-1 right-2 font-normal text-[8px]">*</span>
                                        </th>
                                        <th class="text-center font-bold w-8">Var.</th>
                                        <th class="text-center font-bold w-8">Actual</th>
                                        <th class="text-center font-bold w-8">Previo</th>
                                        <th class="text-center font-bold w-8">12m</th>
                                    </tr>
                                </thead>
                                <tbody class="text-xs">
                                    <tr class="h-10 border-b border-gray-100">
                                        <td class="pl-2">%6$s</td> <!-- Reutilizamos Fecha (Arg 6) -->
                                        <td class="pl-2">DNI</td>
                                        <td class="pl-2">%2$s</td> <!-- Reutilizamos DNI (Arg 2) -->
                                        <td class="pl-2 uppercase">%5$s</td> <!-- Reutilizamos Nombre (Arg 5) -->
                                        <td class="text-center">0.000</td>
                                        <td class="text-right pr-4">0.00</td>
                                        <td class="text-center">
                                            <i class="far fa-file-alt text-gray-500 text-lg border border-gray-400 p-0.5 rounded-sm"></i>
                                        </td>
                                        <td class="text-center">
                                            <div class="w-3.5 h-3.5 bg-gray-300 rounded-full border border-gray-400 mx-auto box-border shadow-inner"></div>
                                        </td>
                                        <td class="text-center">
                                             <div class="w-3.5 h-3.5 bg-gray-300 rounded-full border border-gray-400 mx-auto box-border shadow-inner"></div>
                                        </td>
                                        <td class="text-center relative h-10 w-8 align-middle">
                                            <!-- Icono mitad verde mitad gris -->
                                            <div class="w-4 h-4 rounded-full border border-gray-400 mx-auto overflow-hidden relative bg-gray-100">
                                                <div class="absolute right-0 top-0 w-2 h-4 bg-green-500"></div>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                
                            <!-- Footer / Pie de Página -->
                            <div class="border-t border-gray-400 pt-2 flex justify-between items-start text-xs font-bold mt-8">
                                <div class="flex-1">
                                    Usuario: <span class="font-normal">47936575 -MARIA QUISPE</span>
                                </div>
                                <div class="flex-1 text-center">
                                    Fecha y Hora de creación: <span class="font-normal">%3$s</span> <!-- Reutilizamos Fecha Larga (Arg 3) -->
                                </div>
                                <div class="flex-1 text-right">
                                    Pag. <span class="font-normal ml-4">1 / 6</span>
                                </div>
                            </div>
                
                            <!-- Disclaimer legal -->
                            <div class="text-center text-[10px] text-black mt-1 leading-tight">
                                El presente reporte y puntajes se basan exclusivamente en información objetiva obtenida de fuentes públicas y privadas.<br>
                                No se ha utilizado opiniones ni criterios subjetivos.
                            </div>
                
                        </div>
                
                    </div>
                </body>
                </html>
                """.formatted(
                    colorScore, // 1
                    dni,        // 2
                    fechaActual,// 3
                    dni,        // 4
                    nombreCompleto, // 5 (REEMPLAZADO)
                    fechaActual.substring(0, 10), // 6
                    dni,        // 7
                    colorScore, // 8
                    score,      // 9
                    textoRiesgo,// 10
                    colorScore, // 11
                    score       // 12
            );

            // 3. ENVIAR A PDFSHIFT (Con x-api-key)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey.trim());

            Map<String, Object> body = new HashMap<>();
            body.put("source", htmlTemplate);
            body.put("landscape", false);
            body.put("format", "A4");
            body.put("sandbox", false);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            System.out.println(">>> Enviando diseño propio a PDFShift...");
            ResponseEntity<byte[]> response = restTemplate.postForEntity(API_PDFSHIFT, requestEntity, byte[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new SoaException("502", "Error PDFShift: " + response.getStatusCode());
            }

            // 4. GUARDAR
            byte[] pdfBytes = response.getBody();
            String nombreArchivo = "Reporte_Experian_" + dni + ".pdf";
            return documentoSupportService.guardarArchivoDesdeBytes(pdfBytes, nombreArchivo, codExpediente, "riesgos");

        } catch (Exception e) {
            e.printStackTrace();
            throw new SoaException("503", "Fallo en integración SOA: " + e.getMessage());
        }
    }

}
