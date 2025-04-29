$(document).ready(function () {
    const grupoInput = document.getElementById('buscar-grupos');
    const individualInput = document.getElementById('buscar-individuales');
    const historialInput = document.getElementById('buscar-historial');
    const listaGrupos = document.getElementById('lista-grupos');
    const listaIndividuales = document.getElementById('lista-individuales');
    const listaHistorial = document.getElementById('lista-historial');
    const asignarSelect = document.getElementById('asignar_a');
    const gruposTarea = document.getElementById("gruposTarea");
    const gruposAviso = document.getElementById("gruposAviso");
    const alumnosTarea = document.getElementById("alumnosTarea");
    const alumnosAviso = document.getElementById("alumnosAviso");


// MODAL: Añadir Individuo
    const addIndividualModal = document.getElementById('addIndividualModal');
    const addIndividualBtn = document.getElementById('add-individual-btn');
    const closeButtons = document.querySelectorAll('.close-button');
    const addIndividualForm = document.getElementById('addIndividualForm');
    const groupSelectionList = document.getElementById('group-selection-list');
    const buscarGruposIndividualInput = document.getElementById('buscar-grupos-individual');
    const profesorSelectionList = document.getElementById('profesor-selection-list');
    const buscarProfesorIndividualInput = document.getElementById('buscar-profesor-individual');

// MODAL: Añadir Grupo
    const addGroupModal = document.getElementById('addGroupModal');
    const addGroupBtn = document.getElementById('add-group-btn');
    const closeGroupButtons = document.querySelectorAll('.close-group-button');
    const addGroupForm = document.getElementById('addGroupForm');
    const profesorGrupoSelectionList = document.getElementById('profesor-grupo-selection-list');
    const buscarProfesorGrupoInput = document.getElementById('buscar-profesor-grupo');


// =================== Funciones reutilizables ===================
    function filterList(input, list, itemClass) {
        const filter = input.value.toUpperCase();
        const items = list.getElementsByClassName(itemClass);
        for (let item of items) {
            const text = item.textContent.toUpperCase();
            item.style.display = text.includes(filter) ? "" : "none";
        }
    }

// =================== Modal: Añadir Individuo ===================

    addIndividualBtn.addEventListener('click', () => {
        document.querySelectorAll('#lista-grupos .grupo-item').forEach(grupo => {
            const nombreGrupo = grupo.textContent.trim();
            const radioItem = document.createElement('div');
            radioItem.classList.add('group-radio-item');
            radioItem.innerHTML = `
            <input type="radio" name="grupo-seleccionado" value="${nombreGrupo}">
            <label>${nombreGrupo}</label>`;
            groupSelectionList.appendChild(radioItem);
        });

        document.querySelectorAll('#lista-grupos .profesor-item').forEach(profe => {
            const nombreProfe = profe.textContent.trim();
            const radioItem = document.createElement('div');
            radioItem.classList.add('group-radio-item');
            radioItem.innerHTML = `
            <input type="radio" name="grupo-seleccionado" value="${nombreProfe}">
            <label>${nombreProfe}</label>`;
            profesorSelectionList.appendChild(radioItem);
        });
        addIndividualModal.style.display = "block";
    });

    closeButtons.forEach(btn => btn.addEventListener('click', () => addIndividualModal.style.display = "none"));
    window.addEventListener('click', e => {
        if (e.target === addIndividualModal) addIndividualModal.style.display = "none"
    });

    addIndividualForm.addEventListener('submit', e => {
        e.preventDefault();
        const nombre = document.getElementById('nombre-individual').value;
        const apellido = document.getElementById("apellido-individual").value;
        const telefono = document.getElementById('telefono-individual').value;
        const profesor = selectProfesor;
        const grupo = selectedGroup;
        // Crear objeto con los datos del usuario
        let alumnoData = {nombre, apellido, telefono, profesor, grupo};
        $.ajax({
            url: '/alumno/create', // URL del endpoint de registro
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(alumnoData), // Convertir el objeto a JSON
            success: function (response) {
                // Si el registro es exitoso

                window.location.href = '/dashboard.html'; // Redireccionar al formulario de login
            },
            error: function (xhr, status, error) {
                console.log("Error")
            }
        });
    });

// =================== Modal: Añadir Grupo ===================

    addGroupBtn.addEventListener('click', () => {
        addGroupModal.style.display = "block";
    });

    closeGroupButtons.forEach(btn => btn.addEventListener('click', () => addGroupModal.style.display = "none"));
    window.addEventListener('click', e => {
        if (e.target === addGroupModal) addGroupModal.style.display = "none"
    });

    addGroupForm.addEventListener('submit', e => {
        e.preventDefault();
        const nombre = document.getElementById('nombre-grupo').value;
        const alumnos = selectedAlumnos;
        const profesor = selectProfesor;
        let grupoData = {nombre, alumnos, profesor};
        $.ajax({
            url: '/grupo/create',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(grupoData),
            success: function (response) {
                window.location.href = '/dashboard.html';
            },
            error: function (xhr, status, error) {
                console.log("Error")
            }
        });
    });

    document.getElementById('form-tarea').addEventListener('submit', function (e) {
        e.preventDefault();
        let buttonTarea = document.getElementById("button-tarea");
        buttonTarea.disabled = true;

        const descripcion = document.getElementById('descripcion').value;
        const fecha_limite = document.getElementById('fecha_limite').value;
        const asignar_a = document.getElementById('asignar_a').value;
        let profesorId;

        $.ajax({
            url: "/user",
            method: "GET",
            dataType: "text" // Se lee como texto para evitar parseos automáticos de JSON
        })
            .done((data) => {
                try {
                    const jsonData = JSON.parse(data);
                    profesorId = jsonData[3];

                    let tareaData = {
                        descripcion,
                        fecha_limite,
                        asignar_a,
                        profesorId
                    };

                    $.ajax({
                        url: '/tareas/crear',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(tareaData),
                        success: function (response) {
                            // Aqui se puede mostrar un mensaje
                            console.log("Tarea creada correctamente");
                            window.location.href = "/dashboard.html";

                        },
                        error: function (xhr, status, error) {
                            console.error("Error al crear la tarea:", error);
                            buttonTarea.disabled = false;
                        }
                    });

                } catch (e) {
                    console.error("Error al parsear JSON:", e);
                }
            })
            .fail((jqXHR) => {
                console.error("Error en la solicitud:", jqXHR.responseText);
            });

    });

    document.addEventListener("click", (e) => {
        if (e.target.id === "button-Eliminar-Tarea") {
            let id = document.getElementById("id-tarea").textContent;
            console.log("Eliminando Tarea con ID:", id);

            $.get(`tareas/delete/${id}`)
                .done(() => {
                    console.log("Tarea eliminada correctamente");
                    window.location.href = "/dashboard.html";
                })
                .fail(() => {
                    console.error("Error al eliminar la tarea");
                    window.location.href = "/dashboard.html";
                });
        }
    });


// =================== Filtros de búsqueda ===================

    grupoInput.addEventListener('keyup', () => renderGroups(grupoInput.value));
    individualInput.addEventListener('keyup', () => renderAlumnos(individualInput.value));
    historialInput.addEventListener('keyup', () => renderTareas(historialInput.value));

    buscarGruposIndividualInput.addEventListener('keyup', () => renderGroups(buscarGruposIndividualInput.value));
    buscarProfesorIndividualInput.addEventListener('keyup', () => renderUsers(buscarProfesorIndividualInput.value));
    buscarProfesorGrupoInput.addEventListener('keyup', () => renderUsers(buscarProfesorGrupoInput.value));


// =================== Inicialización ===================


//PROFESORES

// Definimos un array vacío para almacenar los usuarios
    let initialProfesores = [];
    let selectProfesor = null;
// Llamamos a la función getUsers para cargar los usuarios
    getUsers();

// Función para obtener los usuarios del servidor
    function getUsers() {
        $.get("/users", (data) => {
        })
            .done((data) => {
                // Almacenamos los usuarios obtenidos en el array
                initialProfesores = data;
                // Llamamos a la función renderUsers para mostrar los usuarios
                renderUsers();
            })
            .fail((error) => alert(error));
    }

    let selectedProfesorDiv = null;

    function renderUsers(filter = "") {
        profesorSelectionList.innerHTML = "";
        profesorGrupoSelectionList.innerHTML = "";

        initialProfesores
            .filter(user => user.username.toLowerCase().includes(filter.toLowerCase()))
            .forEach(user => {
                const div = document.createElement("div");
                div.className = "user-item p-1 border-bottom";
                div.textContent = user.username;
                div.style.cursor = "pointer";
                div.onclick = () => {
                    if (selectedProfesorDiv) {
                        selectedProfesorDiv.style.backgroundColor = "";
                    }
                    div.style.backgroundColor = "rgba(0, 123, 255, 0.2)"; //Color azul transparente
                    selectedProfesorDiv = div;
                    selectUser(user);
                };
                profesorSelectionList.appendChild(div);
            });
        //Listado para los profesores al crear grupo
        initialProfesores
            .filter(user => user.username.toLowerCase().includes(filter.toLowerCase()))
            .forEach(user => {
                const div = document.createElement("div");
                div.className = "user-item p-1 border-bottom";
                div.textContent = user.username;
                div.style.cursor = "pointer";
                div.onclick = () => {
                    if (selectedProfesorDiv) {
                        selectedProfesorDiv.style.backgroundColor = "";
                    }
                    div.style.backgroundColor = "rgba(0, 123, 255, 0.2)"; //Color azul transparente
                    selectedProfesorDiv = div;
                    selectUser(user);
                };
                profesorGrupoSelectionList.appendChild(div);
            });
    }

    function selectUser(user) {
        selectProfesor = user;
        console.log(selectProfesor)
        return selectProfesor;
    }

//HISTORIAL DE TAREAS
    let initialTareas = [];
    let idProfesor = null;

    getUser();
    getTareas();

    function getUser() {
        $.ajax({
            url: "/user",
            method: "GET",
            dataType: "text"
        }).done((data) => {
            try {
                const jsonData = JSON.parse(data);
                idProfesor = jsonData[3];
                renderTareas();
            } catch (e) {
                console.error("Error al parsear JSON:", e);
            }
        }).fail((jqXHR) => {
            console.error("Error en la solicitud:", jqXHR.responseText);
        });
    }

    function getTareas() {
        $.get("/tareas/history", (data) => {
        })
            .done((data) => {
                initialTareas = data;
                renderTareas(); // solo si ya tienes los datos de usuario
            })
            .fail((error) => alert(error));
    }

    function renderTareas(filter = "") {
        if (idProfesor === null) return; // si aún no se ha cargado el usuario, no continues

        listaHistorial.innerHTML = "";

        initialTareas
            .filter(tarea => tarea.mensaje.toLowerCase().includes(filter.toLowerCase()))
            .forEach(tarea => {
                if (tarea.profesor.id == idProfesor){
                    const div = document.createElement("div");
                    div.className = "list-group-item historial-item";
                    div.style.cursor = "default";
                    div.innerHTML =
                        `<i class="bi bi-check-all"></i>
                    <span id="id-tarea" hidden="hidden">${tarea.id}</span> 
                    ${tarea.mensaje} - ${tarea.fecha_fin ? tarea.fecha_fin : 'sin fecha de entrega'} 
                    <button class="btn btn-danger" id="button-Eliminar-Tarea">Eliminar</button>`;
                    listaHistorial.appendChild(div);
                }
            });
    }


//INDIVIDUOS / ALUMNOS
    let initialIndividuales = [];
    getAlumnos();

    function getAlumnos() {
        $.get("/alumnos", (data) => {
        })
            .done((data) => {
                initialIndividuales = data;
                renderAlumnos();
            })
            .fail((error) => alert(error));
    }

    const selectedAlumnos = [];

    function renderAlumnos(filter = "") {
        listaIndividuales.innerHTML = "";
        initialIndividuales
            .filter(alumno => alumno.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(alumno => {
                const enlace = document.createElement("a")
                enlace.href = `/alumno/edit/${alumno.id}`
                enlace.style.textDecoration = "none"
                enlace.style.color = "inherit"

                listaIndividuales.appendChild(enlace);

                const div = document.createElement("div");
                div.className = "user-item p-1 border-bottom";
                div.textContent = alumno.nombre;
                div.style.cursor = "pointer";
                enlace.appendChild(div);
            });
        initialIndividuales
            .filter(alumno => alumno.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(alumno => {
                const option = document.createElement("option");
                option.value = alumno.id;
                option.textContent = alumno.nombre;
                alumnosTarea.appendChild(option);
            });
        initialIndividuales
            .filter(alumno => alumno.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(alumno => {
                const option = document.createElement("option");
                option.value = alumno.id;
                option.textContent = alumno.nombre;
                alumnosAviso.appendChild(option);
            });
    }

//GRUPOS
    let initialGroups = [];
    let selectedGroup = null;
    getGroups();

    function getGroups() {
        $.get("/grupos", (data) => {
        })
            .done((data) => {
                initialGroups = data;
                console.log(initialGroups)
                renderGroups();
            })
            .fail((error) => alert(error));
    }

    function renderGroups(filter = "") {
        listaGrupos.innerHTML = "";
        groupSelectionList.innerHTML = "";

        initialGroups
            .filter(grupo => grupo.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(grupo => {
                const enlace = document.createElement("a")
                enlace.href = `/grupo/edit/${grupo.id}`
                enlace.style.textDecoration = "none"
                enlace.style.color = "inherit"
                listaGrupos.appendChild(enlace);

                const div = document.createElement("div");
                div.className = "user-item p-1 border-bottom";
                div.textContent = grupo.nombre;
                div.style.cursor = "pointer";
                enlace.appendChild(div);
            });
        //Lista de grupos en el PopUp de Alumnos
        let selectedGrupoDiv = null;
        initialGroups
            .filter(grupo => grupo.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(grupo => {
                const div = document.createElement("div");
                div.className = "user-item p-1 border-bottom";
                div.textContent = grupo.nombre;
                div.style.cursor = "pointer";
                div.onclick = () => {
                    if (selectedGrupoDiv) {
                        selectedGrupoDiv.style.backgroundColor = "";
                    }
                    div.style.backgroundColor = "rgba(0, 123, 255, 0.2)"; //Color azul transparente
                    selectedGrupoDiv = div;
                    selectGroup(grupo);
                };
                groupSelectionList.appendChild(div);
            });
        selectedGrupoDiv = null;
        initialGroups
            .filter(grupo => grupo.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(grupo => {
                const option = document.createElement("option");
                option.value = grupo.id;
                option.textContent = grupo.nombre;
                gruposTarea.appendChild(option);
            });
        initialGroups
            .filter(grupo => grupo.nombre.toLowerCase().includes(filter.toLowerCase()))
            .forEach(grupo => {
                const option = document.createElement("option");
                option.value = grupo.id;
                option.textContent = grupo.nombre;
                gruposAviso.appendChild(option);
            });
    }

    function selectGroup(grupo) {
        selectedGroup = grupo;
        return selectedGroup;
    }
})

