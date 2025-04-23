//NOMBRE DEL TÍTULO
$(document).ready(function () {
    // Se realiza una solicitud AJAX a la URL "/user"
    $.ajax({
        url: "/user",
        method: "GET",
        dataType: "text" // Se lee como texto para evitar parseos automáticos de JSON
    })
        .done((data) => {
            console.log("Contenido de la respuesta:", data);
            try {
                const jsonData = JSON.parse(data);
                console.log("JSON parseado:", jsonData);
                //Titulo del dashboard
                $("#username").text(`Buenas ${jsonData[0]}!`);
            } catch (e) {
                console.error("Error al parsear JSON:", e);
            }
        })
        .fail((jqXHR) => {
            console.error("Error en la solicitud:", jqXHR.responseText);
        });
})