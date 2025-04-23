$(document).ready(function () {
    $('#form-editar-grupo').submit(function (event) {
        event.preventDefault(); // Evita el submit tradicional

        const formData = {
            id: $('#id').val(),
            nombre: $('#nombre').val(),
            profesor: { id: $('#profesor').val() }
        };

        $.ajax({
            url: '/grupo/editar', // endpoint que definimos en Spring
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                window.location.href = "/dashboard.html"; // redirige donde tú quieras
            },
            error: function (xhr) {
                alert("Error al actualizar el grupo: " + xhr.responseText);
            }
        });
    });

    $('#button-eliminar-grupo').click((e) => {
        let id = $('#id').val();

        console.log("Eliminando Grupo con ID:", id);

        $.get(`/grupo/delete/${id}`)
            .done(() => {
                window.location.href = "/dashboard.html";
            })
            .fail((xhr) => {
                alert(xhr.responseText);
                window.location.href = "/dashboard.html";
            });
    });
});
