$(document).ready(function () {
    $('#form-editar-alumno').submit(function (event) {
        event.preventDefault(); // Evita el submit tradicional

        const telefonoValido = /^\+?[\d\s\-()]{9,15}$/.test($('#telefono').val());

        if (!telefonoValido) {
            alert("Por favor, introduce un número de teléfono válido.");
        } else {
            console.log("Número de teléfono válido:", telefonoValido);

            const formData = {
                id: $('#alumnoId').val(),
                nombre: $('#nombre').val(),
                apellido: $('#apellido').val(),
                telefono: $('#telefono').val(),
                profesor: {id: $('#profesor').val()},
                grupo: {id: $('#grupo').val()}
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
        }
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
    const link = "https://t.me/athmos_bot?start=ALUMNO_"+$('#alumnoId').val();
    QRCode.toCanvas(document.getElementById('qrcode'), link, {
        width: 256,
        margin: 1,
        color: {
            dark: "#000000",
            light: "#ffffff"
        }
    }, function (error) {
        if (error) {
            console.error("Error generando el QR:", error);
        } else {
            console.log("QR generado correctamente");
            const urlEl = document.getElementById('urlQr');
            urlEl.href = link;
            urlEl.textContent = link;
        }
    });


});
