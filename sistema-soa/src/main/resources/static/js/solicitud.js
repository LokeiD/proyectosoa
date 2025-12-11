const API_URL = 'http://localhost:8080/api/v1';
const txtDniBuscar = document.getElementById('txtDniBuscar');

document.addEventListener('DOMContentLoaded', () => {
    cargarListasDesplegables();
});

async function cargarListasDesplegables() {
    try {
        const [prodRes, recuRes, riesRes, periRes] = await Promise.all([
            fetch(`${API_URL}/creditos/productos`),
            fetch(`${API_URL}/creditos/recurrencias`),
            fetch(`${API_URL}/creditos/riesgos`),
            fetch(`${API_URL}/creditos/periodos`)
        ]);

        if (!prodRes.ok || !recuRes.ok || !riesRes.ok || !periRes.ok) {
            throw new Error('Error conectando con los catálogos del servicio');
        }

        const productos = await prodRes.json();
        const recurrencias = await recuRes.json();
        const riesgos = await riesRes.json();
        const periodos = await periRes.json();

        llenarSelect('cboProducto', productos, 'codProducto', 'nombre');
        llenarSelect('cboRecurrencia', recurrencias, 'codRecurrencia', 'nombre');
        llenarSelect('cboRiesgo', riesgos, 'codRiesgo', 'descripcion');
        llenarSelect('cboPeriodo', periodos, 'codPeriodo', 'descripcion');

    } catch (error) {
        console.error(error);
        alert('Error: No se pudieron cargar las listas del formulario.');
    }
}

function llenarSelect(idSelect, lista, idCampo, nombreCampo) {
    const select = document.getElementById(idSelect);
    select.innerHTML = '<option selected disabled>-- Seleccione --</option>';

    lista.forEach(item => {
        const option = document.createElement('option');
        option.value = item[idCampo];
        option.textContent = item[nombreCampo];
        select.appendChild(option);
    });
}

txtDniBuscar.addEventListener('blur', async function() {
    const dni = this.value.trim();

    if (dni.length !== 8) return;

    try {
        document.body.style.cursor = 'wait';
        limpiarCamposSocio();

        const response = await fetch(`${API_URL}/socio/buscar/${dni}`);

        if (!response.ok) {
            const mensajeError = await response.text();
            throw new Error(mensajeError);
        }

        const socio = await response.json();
        llenarFormularioSocio(socio);

    } catch (error) {
        limpiarCamposSocio();
        alert("⚠️ ATENCIÓN:\n" + error.message);
    } finally {
        document.body.style.cursor = 'default';
    }
});

function llenarFormularioSocio(socio) {
    document.getElementById('txtDniResultado').value = socio.dni;
    document.getElementById('txtNombreCompleto').value = socio.nombresCompletos;
    document.getElementById('txtNRegistro').value = socio.codSocio;
    document.getElementById('txtEdad').value = socio.edad + ' AÑOS';
    document.getElementById('txtFechaNacimiento').value = socio.fechaNacimiento;
    document.getElementById('txtEstadoCivil').value = socio.estadoCivil;

    document.getElementById('txtDomicilio').value = socio.domicilio;
    document.getElementById('txtDepartamento').value = socio.departamento;
    document.getElementById('txtProvincia').value = socio.provincia;
    document.getElementById('txtDistrito').value = socio.distrito;
    document.getElementById('txtSector').value = socio.sector;
}

const txtTem = document.getElementById('txtTem');
const txtTea = document.getElementById('txtTea');

txtTem.addEventListener('input', function() {
    const valorTem = parseFloat(this.value);

    if (!isNaN(valorTem) && valorTem >= 0) {
        const temDecimal = valorTem / 100;
        const teaDecimal = Math.pow((1 + temDecimal), 12) - 1;
        const teaPorcentaje = (teaDecimal * 100).toFixed(2);
        txtTea.value = teaPorcentaje;
    } else {
        txtTea.value = '';
    }
});
//-Registro de solicitud
const btnRegistrar = document.getElementById('btnRegistrar');

btnRegistrar.addEventListener('click', async function() {
    const dniSocio = document.getElementById('txtDniResultado').value;

    const datosSolicitud = {
        dniSocio: dniSocio,
        montoSolicitado: document.getElementById('txtMonto').value,
        codProducto: document.getElementById('cboProducto').value,
        codRecurrencia: document.getElementById('cboRecurrencia').value,
        codRiesgo: document.getElementById('cboRiesgo').value,
        codPeriodo: document.getElementById('cboPeriodo').value,
        actividad: document.getElementById('txtActividad').value,
        codTrabajador: 1 // HARCODED: Pon un ID de trabajador que exista en tu BD
    };

    if (!dniSocio) {
        alert("⚠️ Error: Debes buscar y validar un socio primero.");
        return;
    }
    if (datosSolicitud.montoSolicitado <= 0) {
        alert("⚠️ Error: El monto debe ser mayor a 0.");
        return;
    }
    if (isNaN(datosSolicitud.codProducto) || isNaN(datosSolicitud.codRecurrencia)) {
        alert("⚠️ Error: Complete todos los campos de selección.");
        return;
    }

    try {
        btnRegistrar.disabled = true;
        btnRegistrar.textContent = "Procesando...";

        const response = await fetch(`${API_URL}/creditos/solicitud`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosSolicitud)
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg);
        }

        const solicitudCreada = await response.json();
        alert(`✅ ¡Solicitud Registrada con Éxito!\nN° de Expediente: ${solicitudCreada.codExpediente}`);
        location.reload();

    } catch (error) {
        alert("❌ Error al registrar: " + error.message);
    } finally {
        btnRegistrar.disabled = false;
        btnRegistrar.textContent = "REGISTRAR SOLICITUD";
    }
});

function limpiarCamposSocio() {
    const ids = [
        'txtDniResultado', 'txtNombreCompleto', 'txtNRegistro',
        'txtEdad', 'txtFechaNacimiento', 'txtEstadoCivil',
        'txtDomicilio', 'txtDepartamento', 'txtProvincia',
        'txtDistrito', 'txtSector'
    ];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

document.addEventListener('DOMContentLoaded', () => {
    listarSolicitudes();

});


const DOCUMENTOS_REQUERIDOS = [
    { nombre: 'FICHA SOCIOECONOMICA', clave: 'ficha' },
    { nombre: 'VISITA DE CAMPO', clave: 'visita' },
    { nombre: 'DNI DEL ASOCIADO', clave: 'dni' },
    { nombre: 'DOCUMENTO DE DOMICILIO', clave: 'domicilio' },
    { nombre: 'CENTRAL DE RIESGOS', clave: 'riesgos' },
    { nombre: 'FLUJO DE EFECTIVO', clave: 'flujo' },
    { nombre: 'ACREDITACION DE INGRESOS', clave: 'ingresos' },
    { nombre: 'OTROS DOCUMENTOS', clave: 'otros' }
];

async function listarSolicitudes() {
    try {
        const response = await fetch('http://localhost:8080/api/v1/creditos/listado');

        if (!response.ok) {
            throw new Error('Error al obtener las solicitudes');
        }

        const solicitudes = await response.json();
        console.log("Datos recibidos:", solicitudes);
        renderizarTabla(solicitudes);

    } catch (error) {
        console.error('Error:', error);
    }
}

function renderizarTabla(datos) {
    const tbody = document.getElementById('tablaCreditos');
    tbody.innerHTML = '';

    datos.forEach(solicitud => {

        const id = solicitud.codExpediente;
        const fecha = solicitud.fechaSolicitud;
        const monto = solicitud.montoSolicitado;
        const socioObj = solicitud.socio || {};

        let nombreSocio = '';
        if (socioObj.nombresCompletos) {
            nombreSocio = socioObj.nombresCompletos;
        } else {
            const n = socioObj.nombres || socioObj.nombre || '';
            const ap = socioObj.apellidoPaterno || '';
            const am = socioObj.apellidoMaterno || '';
            nombreSocio = `${n} ${ap} ${am}`.trim();
        }

        if (!nombreSocio) nombreSocio = 'SOCIO SIN NOMBRE';

        const dniSocio = socioObj.dni || '---';
        const nombreProducto = solicitud.producto?.nombreProducto || '---';
        const periodo = solicitud.periodo?.nombrePeriodo || '---';
        const cuotas = solicitud.cuotas || 1;
        const tea = solicitud.tea || 0;
        const fila = document.createElement('tr');

        const htmlExpediente = generarColumnaExpediente(id);

        fila.innerHTML = `
            <td class="fw-bold text-center">${id}</td>
            <td>${fecha}</td>
            <td>${dniSocio}</td>
            <td class="text-uppercase">${nombreSocio}</td>
            <td class="text-uppercase">${nombreProducto}</td>
            <td class="fw-bold">S/ ${parseFloat(monto).toFixed(2)}</td>
            <td class="text-center">${cuotas}</td>
            <td class="text-center">${periodo}</td>

            <td style="min-width: 300px;">
                <div class="d-flex flex-column gap-1">
                    ${htmlExpediente}
                </div>
                <button class="btn btn-success w-100 mt-3 btn-sm text-white fw-bold shadow-sm"
                        onclick="enviarAEvaluacion(${id})">
                    <i class="fas fa-paper-plane me-2"></i> ENVIAR A EVALUACIÓN
                </button>
            </td>
        `;

        tbody.appendChild(fila);
    });
}

// Función auxiliar para crear la lista de documentos por cada fila
function generarColumnaExpediente(idSolicitud) {
    let html = '';

    DOCUMENTOS_REQUERIDOS.forEach(doc => {
        html += `
            <div class="d-flex justify-content-between align-items-center border-bottom pb-1 mb-1">
                <span class="small text-muted fw-bold" style="font-size: 0.75rem;">${doc.nombre}</span>
                <div class="btn-group">
                    <button class="btn btn-sm btn-link text-secondary p-0 me-2"
                            title="Subir archivo"
                            onclick="abrirModalCarga('${idSolicitud}', '${doc.clave}')">
                        <i class="fas fa-cloud-upload-alt"></i>
                    </button>

                    <button class="btn btn-sm btn-link text-secondary p-0"
                            title="Editar"
                            onclick="editarArchivo('${idSolicitud}', '${doc.clave}')">
                        <i class="fas fa-pen"></i>
                    </button>
                </div>
            </div>
        `;
    });

    return html;
}


function enviarAEvaluacion(id) {
    alert(`Enviando solicitud ${id} a evaluación...`);
    // Aquí iría tu fetch PUT/POST para cambiar el estado
}

function abrirModalCarga(idSolicitud, tipoDoc) {
    console.log(`Subir documento: ${tipoDoc} para solicitud: ${idSolicitud}`);
    // Aquí podrías disparar el click del input file oculto o abrir un modal
    document.getElementById('globalFileInput').click();
}

function editarArchivo(idSolicitud, tipoDoc) {
    console.log(`Editando documento: ${tipoDoc} para solicitud: ${idSolicitud}`);
}
