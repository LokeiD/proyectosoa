// Apunta directamente a tu servidor Spring Boot
const API_URL = 'http://localhost:8080/api/v1/cuentas-ahorro';

const tablaCuerpo = document.getElementById('tablaCaptaciones');
const inputBusqueda = document.getElementById('searchInput');

// Instancias de los Modals de Bootstrap
let modalRetiroBootstrap = null;
let modalAhorroBootstrap = null;

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar modales
    modalRetiroBootstrap = new bootstrap.Modal(document.getElementById('modalRetiro'));
    modalAhorroBootstrap = new bootstrap.Modal(document.getElementById('modalAhorro'));

    // Cargar datos iniciales
    listarCaptaciones();
});

// --- 1. LISTAR CAPTACIONES (Desde BD) ---
async function listarCaptaciones() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Error al obtener datos");

        const datos = await response.json();
        renderizarTabla(datos);
    } catch (error) {
        console.error(error);
        tablaCuerpo.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error cargando datos</td></tr>';
    }
}

function renderizarTabla(data) {
    tablaCuerpo.innerHTML = '';

    if (!data || data.length === 0) {
        // Ajustamos el colspan a 6 porque ahora hay menos columnas
        tablaCuerpo.innerHTML = '<tr><td colspan="6" class="text-center py-4">No hay cuentas registradas</td></tr>';
        return;
    }

    data.forEach(cuenta => {
        const id = cuenta.codCuenta;
        const fecha = cuenta.fechaApertura || '---';
        const saldo = (cuenta.saldo !== undefined && cuenta.saldo !== null)
                      ? parseFloat(cuenta.saldo).toFixed(2)
                      : '0.00';

        // Datos del DTO (ahora vienen directos)
        const dni = cuenta.dni || '---';
        const nombreCompleto = cuenta.nombreCompleto || '---';

        const tr = document.createElement('tr');
        // Eliminamos los <td> de interes y tasa
        tr.innerHTML = `
            <td>${id}</td>
            <td>${fecha}</td>
            <td>${dni}</td>
            <td class="text-uppercase">${nombreCompleto}</td>
            <td class="text-center fw-bold">S/ ${saldo}</td>
            <td style="width: 140px;">
                <div class="d-flex flex-column gap-2">
                    <button class="btn btn-success btn-sm w-100" onclick="prepararAhorro(${id})">
                        <i class="fas fa-arrow-up"></i> AHORRO
                    </button>
                    <button class="btn btn-danger btn-sm w-100" onclick="prepararRetiro(${id})">
                        <i class="fas fa-arrow-down"></i> RETIRO
                    </button>
                </div>
            </td>
        `;
        tablaCuerpo.appendChild(tr);
    });
}

// --- 2. LÓGICA DE MODAL: RETIRO ---
async function prepararRetiro(idCuenta) {
    try {
        const response = await fetch(`${API_URL}/${idCuenta}`);
        const cuenta = await response.json();

        document.getElementById('hdnIdCuentaRetiro').value = cuenta.codCuenta;
        document.getElementById('txtCuentaRetiro').value = cuenta.numeroCuenta || cuenta.codCuenta;

        // Validaciones: Intenta sacar datos del DTO plano, sino busca en objeto anidado
        const dniSocio = cuenta.dni || (cuenta.socio ? cuenta.socio.dni : '');

        let nombreSocio = '';
        if (cuenta.nombreCompleto) {
            nombreSocio = cuenta.nombreCompleto;
        } else if (cuenta.socio) {
            // Unificamos lógica: Nombre + Paterno + Materno
            const mat = cuenta.socio.apellidoMaterno || '';
            nombreSocio = `${cuenta.socio.nombres} ${cuenta.socio.apellidoPaterno} ${mat}`.trim();
        }

        document.getElementById('txtDniRetiro').value = dniSocio;
        document.getElementById('txtNombreRetiro').value = nombreSocio;

        const saldoVal = (cuenta.saldo !== undefined && cuenta.saldo !== null) ? cuenta.saldo : 0;
        document.getElementById('lblSaldoRetiro').textContent = 'S/ ' + parseFloat(saldoVal).toFixed(2);

        document.getElementById('txtMontoRetiro').value = '';

        modalRetiroBootstrap.show();

    } catch (error) {
        console.error(error);
        alert("Error cargando información de la cuenta");
    }
}

// Evento Submit del formulario Retiro
document.getElementById('formRetiro').addEventListener('submit', async function(e) {
    e.preventDefault();

    const idCuenta = document.getElementById('hdnIdCuentaRetiro').value;
    const monto = document.getElementById('txtMontoRetiro').value;

    if(monto <= 0) {
        swal("Error", "Ingrese un monto válido", "warning");
        return;
    }

    try {
        const btn = document.querySelector('#formRetiro button[type="submit"]');
        btn.disabled = true;
        btn.textContent = "Procesando...";

        const formData = new FormData();
        formData.append('monto', monto);

        const response = await fetch(`${API_URL}/${idCuenta}/retiro`, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if(!response.ok) throw new Error(result.error || "Error en la operación");

        swal("Éxito", "Retiro registrado correctamente", "success");
        modalRetiroBootstrap.hide();
        listarCaptaciones();

    } catch (error) {
        swal("Error", error.message, "error");
    } finally {
        const btn = document.querySelector('#formRetiro button[type="submit"]');
        btn.disabled = false;
        btn.textContent = "REGISTRAR RETIRO";
    }
});


// --- 3. LÓGICA DE MODAL: AHORRO (DEPÓSITO) ---
async function prepararAhorro(idCuenta) {
    try {
        const response = await fetch(`${API_URL}/${idCuenta}`);
        const cuenta = await response.json();

        document.getElementById('hdnIdCuentaAhorro').value = cuenta.codCuenta;
        document.getElementById('txtCuentaAhorro').value = cuenta.numeroCuenta || cuenta.codCuenta;

        const dniSocio = cuenta.dni || (cuenta.socio ? cuenta.socio.dni : '');

        let nombreSocio = '';
        if (cuenta.nombreCompleto) {
            nombreSocio = cuenta.nombreCompleto;
        } else if (cuenta.socio) {
            const mat = cuenta.socio.apellidoMaterno || '';
            nombreSocio = `${cuenta.socio.nombres} ${cuenta.socio.apellidoPaterno} ${mat}`.trim();
        }

        document.getElementById('txtDniAhorro').value = dniSocio;
        document.getElementById('txtNombreAhorro').value = nombreSocio;

        const saldoVal = (cuenta.saldo !== undefined && cuenta.saldo !== null) ? cuenta.saldo : 0;
        document.getElementById('lblSaldoAhorro').textContent = 'S/ ' + parseFloat(saldoVal).toFixed(2);

        document.getElementById('txtMontoAhorro').value = '';

        modalAhorroBootstrap.show();

    } catch (error) {
        console.error(error);
        alert("Error cargando información de la cuenta");
    }
}

// Evento Submit del formulario Ahorro
document.getElementById('formAhorro').addEventListener('submit', async function(e) {
    e.preventDefault();

    const idCuenta = document.getElementById('hdnIdCuentaAhorro').value;
    const monto = document.getElementById('txtMontoAhorro').value;

    if(monto <= 0) {
        swal("Error", "Ingrese un monto válido", "warning");
        return;
    }

    try {
        const btn = document.querySelector('#formAhorro button[type="submit"]');
        btn.disabled = true;
        btn.textContent = "Procesando...";

        const formData = new FormData();
        formData.append('monto', monto);

        const response = await fetch(`${API_URL}/${idCuenta}/deposito`, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if(!response.ok) throw new Error(result.error || "Error en la operación");

        swal("Éxito", "Depósito registrado correctamente", "success");
        modalAhorroBootstrap.hide();
        listarCaptaciones();

    } catch (error) {
        swal("Error", error.message, "error");
    } finally {
        const btn = document.querySelector('#formAhorro button[type="submit"]');
        btn.disabled = false;
        btn.textContent = "REGISTRAR AHORRO";
    }
});

// --- 4. BÚSQUEDA ---
inputBusqueda.addEventListener('keyup', function() {
    const term = this.value.toLowerCase();
    const filas = tablaCuerpo.getElementsByTagName('tr');

    for (let i = 0; i < filas.length; i++) {
        const textoFila = filas[i].textContent.toLowerCase();
        if (textoFila.includes(term)) {
            filas[i].style.display = "";
        } else {
            filas[i].style.display = "none";
        }
    }
});