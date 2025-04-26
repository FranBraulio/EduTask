$(document).ready(function () {

    // Obtener elementos del DOM
    let errorModal = new bootstrap.Modal(document.getElementById('errorModal'))
    let text = document.getElementById("textModal");
    let header = document.getElementById("headerModal");
    let title = document.getElementById("titleModal");

    // Evento al enviar el formulario de registro
    $('#registration-form').on('submit', function(e) {
        e.preventDefault(); // Evitar que la página se recargue
        $('#registerButton').prop('disabled', true); // Deshabilitar el botón de registro
        // Obtener valores de los inputs
        let username = $('#username').val();
        let email = $('#email').val();
        let password = $('#password').val();
        let confirmPassword = $('#confirm-password').val();

        // Validar que las contraseñas coincidan
        if (password !== confirmPassword) {
            alert('Las contraseñas no coinciden');
            return;
        }

        // Crear objeto con los datos del usuario
        let profesorData = { username, email, password };
        // Validar la contraseña
        if (validarContrasena($("#password").val())) {
            // enviar datos al endpoint
            if ($("#terms").is(":checked")) {
                $.ajax({
                    url: '/register', // URL del endpoint de registro
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(profesorData), // Convertir el objeto a JSON
                    success: function (response) {
                        // Si el registro es exitoso
                        if (response === "Usuario creado con éxito") {
                            deleteBG(header) // Limpiar el estilo de fondo anterior
                            header.classList.add("bg-success") // Añadir estilo de fondo verde
                            title.textContent = "Usuario creado con éxito"
                            text.textContent = "El usuario ha sido creado con éxito."
                            errorModal.show(); // Mostrar el modal de éxito
                        }
                        window.location.href = '/index#login'; // Redireccionar al formulario de login
                    },
                    error: function (xhr, status, error) {
                        deleteBG(header) // Limpiar el estilo de fondo anterior
                        header.classList.add("bg-danger") // Añadir estilo de fondo rojo
                        title.textContent = "Error"
                        text.textContent = "Usuario ya registrado"
                        errorModal.show(); // Mostrar el modal de error

                        $('#registerButton').prop('disabled', false); // Habilitar el botón de registro
                    }
                });
            } else {
                deleteBG(header) // Limpiar el estilo de fondo anterior
                header.classList.add("bg-warning") // Añadir estilo de fondo amarillo
                title.textContent = "Advertencia"
                text.textContent = "Debes aceptar los términos y condiciones."
                errorModal.show(); // Mostrar el modal de advertencia
                $('#registerButton').prop('disabled', false); // Habilitar el botón de registro
            }
        }else{
            deleteBG(header) // Limpiar el estilo de fondo anterior
            header.classList.add("bg-warning") // Añadir estilo de fondo amarillo
            title.textContent = "Advertencia"
            text.textContent = "Contraseña incorrecta, debe contener al menos 8 caracteres, una mayúscula y una minúscula."
            errorModal.show(); // Mostrar el modal de advertencia
            $('#registerButton').prop('disabled', false); // Habilitar el botón de registro
        }
    });

    // Función para validar la contraseña
    function validarContrasena(contrasena) {
        const regex = /^(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
        return regex.test(contrasena);
    }

    // Función para eliminar el estilo de fondo anterior
    function deleteBG(header){
        header.classList.forEach(clase => {
            if (clase.startsWith("bg-")) {
                header.classList.remove(clase);
            }
        })
    }

    // Evento al escribir en el input de contraseña
    $("#password").on("keyup", function() {
        let contrasena = $(this).val();
        validarRequisito(contrasena.length >= 8, "#caracteres"); // Validar longitud de contraseña
        validarRequisito(/[A-Z]/.test(contrasena), "#mayuscula"); // Validar que contenga al menos una mayúscula
        validarRequisito(/[a-z]/.test(contrasena), "#minuscula"); // Validar que contenga al menos una minúscula
    });

    // Evento al escribir en el input de confirmación de contraseña
    $("#password").on("keyup", function() {
        verificarCoincidencia(); // Verificar que las contraseñas coincidan
    });

    // Función para verificar que las contraseñas coincidan
    function verificarCoincidencia() {
        let password = $("#password").val();
        let confirmPassword = $("#confirm-password").val();
        let errorElement = $("#password-error");

        if (confirmPassword !== "" && password !== confirmPassword) {
            errorElement.removeClass("d-none").addClass("text-danger").text("Las contraseñas no coinciden.").fadeIn();
        } else if (password === ""|| confirmPassword === "") {
            errorElement.text("No puede estar vacío").fadeOut(() => errorElement.addClass("d-none").removeClass("text-danger"));
        }else {
            errorElement.text("").fadeOut(() => errorElement.addClass("d-none").removeClass("text-danger"));
        }
    }

    // Evento al escribir en el input de nombre de usuario
    $("#username").on("keyup", () => {
        let username = $("#username").val();
        validarNombre(username); // Validar el nombre de usuario
    });

    // Función para validar el nombre de usuario
    function validarNombre(username) {
        let errorElement = $("#username-error");
        let regex = /^[a-zA-Z0-9_]+$/;
        if (username === "") {
            mostrarError(errorElement, "El nombre de usuario no puede estar vacío.");
        } else if (username.length < 3) {
            mostrarError(errorElement, "Debe tener al menos 3 caracteres.");
        } else if (!regex.test(username)) {
            mostrarError(errorElement, "Solo se permiten letras, números y guion bajo.");
        } else {
            ocultarError(errorElement);
        }
    }

    // Función para mostrar un error en un elemento
    function mostrarError(element, mensaje) {
        element.removeClass("d-none").addClass("text-danger").text(mensaje).fadeIn();
    }

    // Función para ocultar un error en un elemento
    function ocultarError(element) {
        element.text("").fadeOut(() => element.addClass("d-none").removeClass("text-danger"));
    }

    // Función para validar un requisito
    function validarRequisito(condicion, etiqueta) {
        if (condicion) {
            $(etiqueta).removeClass("text-danger").addClass("text-success");
            $(etiqueta).find("i").removeClass("bi-x-circle").addClass("bi-check-circle");
        } else {
            $(etiqueta).removeClass("text-success").addClass("text-danger");
            $(etiqueta).find("i").removeClass("bi-check-circle").addClass("bi-x-circle");
        }
    }

    // Evento al hacer click en el botón de mostrar/ocultar contraseña
    document.getElementById('togglePassword').addEventListener('click', function () {
        let passwordInput = document.getElementById('password');
        let icon = this.querySelector('i');
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            icon.classList.replace("bi-eye", "bi-eye-slash");
        } else {
            passwordInput.type = "password";
            icon.classList.replace("bi-eye-slash", "bi-eye");
        }
    });

    // Evento al hacer click en el botón de mostrar/ocultar confirmación de contraseña
    document.getElementById('togglePassword2').addEventListener('click', function () {
        let passwordInput = document.getElementById('confirm-password');
        let icon = this.querySelector('i');
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            icon.classList.replace("bi-eye", "bi-eye-slash");
        } else {
            passwordInput.type = "password";
            icon.classList.replace("bi-eye-slash", "bi-eye");
        }
    });
});