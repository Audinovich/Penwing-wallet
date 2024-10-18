document.addEventListener('DOMContentLoaded', function() {
    var urlParams = new URLSearchParams(window.location.search);
    var customerId = urlParams.get('customerId');

    if (customerId) {
        fetch(`http://localhost:8080/Customer/${customerId}/wallet`)
            .then(response => response.json())
            .then(data => {
                var walletArticlesDiv = document.getElementById('wallet-articles');
                data.forEach(article => {
                    var articleElement = document.createElement('p');
                    articleElement.textContent = `Nombre: ${article.name}, Precio: ${article.price}`;
                    walletArticlesDiv.appendChild(articleElement);
                });
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
});