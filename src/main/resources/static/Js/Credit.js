document.addEventListener('DOMContentLoaded', function() {
    var walletId = document.getElementById('wallet_id').value;
    var customerId = document.getElementById('customer_id').value;

    // Cargar artículos y créditos al cargar la página
    if (walletId) {
        Promise.all([
            fetch(`http://localhost:8080/credit/getCreditById/${customerId}`),
            fetch(`http://localhost:8080/article/getAllArticles`)
        ])
        .then(responses => Promise.all(responses.map(response => {
            if (!response.ok) {
                throw new Error('No se pudo obtener los datos');
            }
            return response.json();
        })))
        .then(([credit, articles]) => {
            displayArticlesAndCredit(articles, credit);
        })
        .catch(error => {
            console.error('Error al obtener los datos:', error);
        });
    } else {
        console.error('No se encontró el walletId en el HTML.');
    }
});

function displayArticlesAndCredit(articles, credit) {
    const walletArticlesDiv = document.getElementById('wallet-articles');
    const euroValueElement = document.getElementById('euro-value'); // Obtener el elemento para euros
    walletArticlesDiv.innerHTML = ''; // Limpia el contenido previo

    if (!articles || articles.length === 0) {
        walletArticlesDiv.innerHTML = '<tr><td colspan="5">No articles available.</td></tr>';
        return;
    }

    // Inicializar total de euros
    let totalEuros = 0;

    // Si credit es un arreglo, puedes usar el primer elemento o la lógica que necesites
    const creditInfo = credit[0]; // Asumiendo que quieres el primer crédito si hay varios
    if (creditInfo) {
        totalEuros = creditInfo.euro || 0; // Obtener el valor de euros del primer crédito
    }

    articles.forEach((article, index) => {
        // Encontrar el crédito asociado al artículo por símbolo
        const creditAmount = creditInfo ? (creditInfo[article.symbol] || 0) : 'N/A';

        // Crear una fila para cada artículo
        const articleRow = `
            <tr>
                <td>${index + 1}</td>
                <td>${article.symbol}</td>
                <td><img src="${article.image}" alt="${article.name}" width="50"></td>
                <td>${article.name}</td>
                <td>${creditAmount}</td>
                <td>${article.currentPrice || 'N/A'}</td>
            </tr>
        `;
        walletArticlesDiv.innerHTML += articleRow;
    });

    // Actualizar el valor total de euros en el HTML
    euroValueElement.innerText = totalEuros.toFixed(2); // Formatear a dos decimales
}
