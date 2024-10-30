document.addEventListener('DOMContentLoaded', function() {
    const welcomeMessage = document.getElementById('welcome-message').innerText; // Obtén el texto del contenedor
    const messageContainer = document.getElementById('welcome-message');

    // Limpiar el contenedor para el efecto de escritura
    messageContainer.innerText = '';

    if (welcomeMessage) {
        let index = 0; // Índice para controlar el carácter actual
        const typingSpeed = 150; // Velocidad de tipeo en milisegundos

        function typeMessage() {
            if (index < welcomeMessage.length) {
                messageContainer.innerText += welcomeMessage.charAt(index); // Añade un carácter al contenedor
                index++; // Aumenta el índice
                setTimeout(typeMessage, typingSpeed);
            }
        }

        // Llamar a la función para iniciar el efecto de tipeo
        typeMessage();
    }
});
