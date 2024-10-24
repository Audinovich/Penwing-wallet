document.addEventListener('DOMContentLoaded', function() {
    var walletId = document.getElementById('wallet_id').value;

    // Cargar información de los artículos al cargar la página
    if (walletId) {
        fetch(`http://localhost:8080/article/getAllArticles`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('No se pudo obtener la información de los artículos');
                }
                return response.json();
            })
            .then(articles => {
                console.log(articles); // Verifica los datos recibidos
                displayArticles(articles);
            })
            .catch(error => {
                console.error('Error al obtener la información de los artículos:', error);
            });
    } else {
        console.error('No se encontró el walletId en el HTML.');
    }

    function displayArticles(articles) {
        // Obtener el tbody donde se mostrarán los artículos
        var tableBody = document.getElementById('wallet-articles');
        tableBody.innerHTML = '';

        // Recorrer la lista de artículos y crear una fila para cada uno
        articles.forEach((article, index) => {
            var row = document.createElement('tr');

            // Crear las celdas con la información del artículo
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${article.symbol || "N/A"}</td>
                <td>${article.name || "No Name Available"}</td>
                <td><img src="${article.image || ""}" alt="${article.name || "No Image"}" width="50"></td>
                <td>${article.currentPrice !== undefined ? article.currentPrice : "N/A"}</td>
                <td>${article.marketCap !== undefined ? article.marketCap : "N/A"}</td>
                <td>${article.totalVolume !== undefined ? article.totalVolume : "N/A"}</td>
                <td>${article.high24h !== undefined ? article.high24h : "N/A"}</td>
                <td>${article.low24h !== undefined ? article.low24h : "N/A"}</td>
                <td>${article.priceChange24h !== undefined ? article.priceChange24h : "N/A"}</td>
                <td>${article.priceChangePercentage24h !== undefined ? article.priceChangePercentage24h : "N/A"}</td>
                <td>${article.ath !== undefined ? article.ath : "N/A"}</td>
                <td>${article.athChangePercentage !== undefined ? article.athChangePercentage : "N/A"}</td>
                <td>${article.lastUpdated ? new Date(article.lastUpdated).toLocaleString() : "N/A"}</td>
            `;

            // Agregar la fila al tbody
            tableBody.appendChild(row);
        });
    }
});
