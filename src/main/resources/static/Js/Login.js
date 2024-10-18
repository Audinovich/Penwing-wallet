document.querySelector('form').addEventListener('submit', function(event) {
    event.preventDefault();

    //SE CAPTURAN LOS VALORES DEL FORMULARIO
    var Customer = {
        name: document.getElementById('name').value,
        surname: document.getElementById('surname').value,
        password: document.getElementById('password').value,
        address: document.getElementById('address').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };

    fetch('http://localhost:8080/customer/saveCustomer', {
        method: 'POST',
        headers: {

            'Content-Type': 'application/json'
        },

        body: JSON.stringify(Customer)
    })
    .then(response => response.json())
    .then(data => console.log(data))

    .catch((error) => {
        console.error('Error:', error);

    });
});