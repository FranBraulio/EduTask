$(document).ready(function () {
    $('#form-editar-alumno').submit(function (event) {
        event.preventDefault(); // Evita el submit tradicional

        const formData = {
            id: $('#alumnoId').val(),
            nombre: $('#nombre').val(),
            apellido: $('#apellido').val(),
            telefono: $('#telefono').val(),
            profesor: { id: $('#profesor').val() },
            grupo: { id: $('#grupo').val() }
        };

        $.ajax({
            url: '/alumnos/editar', // endpoint que definimos en Spring
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                window.location.href = "/dashboard.html"; // redirige donde tú quieras
            },
            error: function (xhr) {
                alert("Error al actualizar alumno: " + xhr.responseText);
            }
        });
    });

    $('#button-Eliminar-Alumno').click((e) => {
        let id = $('#alumnoId').val();

            console.log("Eliminando Tarea con ID:", id);

            $.get(`/alumno/delete/${id}`)
                .done(() => {
                    window.location.href = "/dashboard.html";
                })
                .fail((xhr) => {
                    alert(xhr.responseText);
                    window.location.href = "/dashboard.html";
                });
    });
    const link = "https://t.me/athmos_bot?start="+$('#alumnoId').val();
    QRCode.toCanvas(document.getElementById('qrcode'), link, function (error) {
        if (error) console.error(error);
        console.log('QR generado!');
    });

});
