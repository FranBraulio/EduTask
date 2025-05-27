$(document).ready(function () {
    const listaHistorialAdmin = document.getElementById('lista-historial-admin');
    const historialInput = document.getElementById('buscar-historial');
    const deleteProfesor = document.getElementById("boton-eliminar-profesor");
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
                div.className = "list-group-item historial-item tarea-item";
                div.style.cursor = "default";
                div.innerHTML =
                    `<i class="bi bi-check-all"></i>
                     <span class="id-tarea" hidden="hidden">${tarea.id}</span> 
                     ${tarea.mensaje} - ${tarea.fecha_fin ? tarea.fecha_fin : 'sin fecha de entrega'} 
                     <button class="btn btn-danger button-eliminar-tarea">Eliminar</button>`;

                listaHistorialAdmin.appendChild(div);
            });
    }

    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("button-eliminar-tarea")) {
            const tareaElement = e.target.closest(".tarea-item"); // Contenedor de la tarea
            const id = tareaElement.querySelector(".id-tarea").textContent;
            console.log("Eliminando Tarea con ID:", id);

            $.get(`tareas/delete/${id}`)
                .done(() => {
                    console.log("Tarea eliminada correctamente");
                    window.location.href = "/administrador";
                })
                .fail(() => {
                    console.error("Error al eliminar la tarea");
                    window.location.href = "/administrador";
                });
        }
    });

    deleteProfesor.addEventListener("click", () => {
        var myModal = new bootstrap.Modal(document.getElementById("confirmModal"));
        myModal.show();

        document.getElementById("confirmYes").onclick = () => {
            console.log("Eliminando...");
            myModal.hide();
            const id = document.getElementById("profesor-id").textContent;
            if (!id) {
                console.error("No se encontró el id del profesor");
                return;
            }

            $.get(`/delete/${id}`)
                .done(() => {
                    console.log("Profesor eliminado correctamente");
                    window.location.href = "/administrador"
                })
                .fail(() => {
                    window.location.href = "/administrador"
                });
        }
    });
});