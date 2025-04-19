$(document).ready(function () {
    //Aqui recibimos los datos de los modales
    const perfilModal = new bootstrap.Modal(document.getElementById('perfilModal'));
    const header = document.getElementById("headerModal");
    const title = document.getElementById("titleModal");
    const text = document.getElementById("textModal");

    //Se envia el formulario al hacer submit y se desactiva el boton.
    $("#editProfileForm").on("submit", function (event) {
        event.preventDefault(); // Evita el envío del formulario tradicional
        $("#editButton").attr("disabled", true);


        const username = $("#name").val();
        const email = $("#email").val();
        const password = $("#password").val().trim();
        const confirmPassword = $("#confirm-password").val().trim();

        //Se hacen las verificaciones de los campos del formulario al clickar en el boton
        if (username === "" || email === "") {
            deleteBG(header);
            header.classList.add("bg-warning")
            title.innerHTML = "Aviso"
            text.innerHTML = "Por favor, rellene todos los campos obligatorios."
            perfilModal.show()
            $("#editButton").attr("disabled", false);
            return;
        }

        // Validar que las contraseñas coincidan si se ha ingresado una
        if (password !== "" && password !== confirmPassword) {
            $("#password-error").removeClass("d-none");
            $("#editButton").attr("disabled", false);
            return;
        } else {
            $("#password-error").addClass("d-none");
        }

        // Se genera un objeto literal de usuario el cual trata el servidor despues
        event.preventDefault();
        let profesor = {
            username: username,
            email: email,
            password: password
        }
        console.log(profesor);
        //Ultima validacion del campo contraseña
        if (validarContrasena(password)) {
            //Se envian los datos al servidor, en caso de funcionar correctamente todo se le redigira directamente a login tras 2 segundos.
            $.ajax({
                url: "/editProfile",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(profesor),
                success: function (response) {
                    deleteBG(header);
                    header.classList.add("bg-success")
                    title.innerHTML = "Usuario editado con exito"
                    text.innerHTML = "El profesor ha sido editado con exito."
                    perfilModal.show();
                    console.log("Perfil actualizado:", {username, email, password});
                    setTimeout(function() {
                        window.location.href = "/logout"
                    }, 2000);
                },
                error: function (xhr, status, error) {
                    deleteBG(header);
                    header.classList.add("bg-danger")
                    title.innerHTML = "Error"
                    text.innerHTML = "Error al actualizar el perfil, intentelo de nuevo mas tarde"
                    if (xhr.responseText){
                        text.innerHTML = "Correo en uso"
                    }
                    perfilModal.show()
                    $("#editButton").attr("disabled", false);
                }
            })
        }else{
            //En caso de que la validacion de la contraseña falle se muestra un mensaje de error
            deleteBG(header);
            header.classList.add("bg-warning")
            title.innerHTML = "Aviso"
            text.innerHTML = "Contraseña invalida, recuerda que debe contener al menos 8 caracteres, una mayuscula y una minuscula."
            perfilModal.show()
            $("#editButton").attr("disabled", false);
        }

    });

    // Mostrar/ocultar contraseña
    $(".toggle-password").on("click", function () {
        const targetId = $(this).attr("data-target");
        const input = $("#" + targetId);
        const icon = $(this).find("i");

        if (input.attr("type") === "password") {
            input.attr("type", "text");
            icon.removeClass("bi-eye").addClass("bi-eye-slash");
        } else {
            input.attr("type", "password");
            icon.removeClass("bi-eye-slash").addClass("bi-eye");
        }
    });

    // Tester de contraseña
    function validarContrasena(contrasena) {
        const regex = /^(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
        return regex.test(contrasena);
    }

    //Elimina el fondo del modal
    function deleteBG(header){
        header.classList.forEach(clase => {
            if (clase.startsWith("bg-")) {
                header.classList.remove(clase);
            }
        })
    }
});