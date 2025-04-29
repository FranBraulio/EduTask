$(document).ready(function () {
    const listaHistorialAdmin = document.getElementById('lista-historial-admin');
    const historialInput = document.getElementById('buscar-historial');

    let initialTareas = [];

    getTareas();
    historialInput.addEventListener('keyup', () => renderTareas(historialInput.value));


    function getTareas() {
        $.get("/tareas/history", (data) => {
        })
            .done((data) => {
                initialTareas = data;
                renderTareas();
            })
            .fail((error) => alert(error));
    }

    function renderTareas(filter = "") {
        listaHistorialAdmin.innerHTML = "";

        initialTareas
            .filter(tarea => tarea.mensaje.toLowerCase().includes(filter.toLowerCase()))
            .forEach(tarea => {
                const div = document.createElement("div");
                div.className = "list-group-item historial-item";
                div.style.cursor = "default";
                div.innerHTML =
                    `<i class="bi bi-check-all"></i>
                     <span id="id-tarea" hidden="hidden">${tarea.id}</span> 
                     ${tarea.mensaje} - ${tarea.fecha_fin ? tarea.fecha_fin : 'sin fecha de entrega'} 
                     <button class="btn btn-danger" id="button-Eliminar-Tarea">Eliminar</button>`;

                listaHistorialAdmin.appendChild(div);
            });
    }

});