document.addEventListener('DOMContentLoaded', function() {
    const walletId = document.getElementById('wallet_id').value;
    const customerId = document.getElementById('customer_id').value;

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
    const euroValueElement = document.getElementById('euro-value');
    walletArticlesDiv.innerHTML = '';
    let totalEuros = 0;

    const creditInfo = credit[0];
    if (creditInfo) {
        totalEuros = creditInfo.euro || 0;
    }

    articles.forEach((article, index) => {
        const creditAmount = creditInfo ? (creditInfo[article.symbol] || 0) : 'N/A';

        const articleRow = `
            <tr>
                <td>${index + 1}</td>
                <td>${article.symbol}</td>
                <td><img src="${article.image}" alt="${article.name}" width="50"></td>
                <td>${article.name}</td>
                <td>${creditAmount}</td>
                <td>${article.currentPrice || 'N/A'}</td>
                <td>
                    <button class="btn btn-success btn-sm" onclick="openTransactionModal('buy', '${article.symbol}', ${article.currentPrice})">Buy</button>
                    <button class="btn btn-danger btn-sm" onclick="openTransactionModal('sell', '${article.symbol}', ${article.currentPrice})">Sell</button>
                </td>
            </tr>
        `;
        walletArticlesDiv.innerHTML += articleRow;
    });

    euroValueElement.innerText = totalEuros.toFixed(2);
}

function openTransactionModal(type, symbol, currentPrice) {
    $('#transactionModal').modal('show');
    document.getElementById('transaction-type').value = type;
    document.getElementById('selected-symbol').value = symbol;
    document.getElementById('current-price').value = currentPrice;

    document.getElementById('transaction-info').innerText =
        type === 'buy'
            ? `You need ${currentPrice} € per unit to buy.`
            : `Selling ${symbol}`;
}

document.getElementById('confirm-transaction').addEventListener('click', function() {
    const amount = parseFloat(document.getElementById('crypto-amount').value);
    const type = document.getElementById('transaction-type').value;
    const symbol = document.getElementById('selected-symbol').value;
    const currentPrice = parseFloat(document.getElementById('current-price').value);
    const euroBalance = parseFloat(document.getElementById('euro-value').innerText);


    confirmTransaction(amount, type, symbol);
});

function confirmTransaction(amount, type, symbol) {
    const customerId = document.getElementById('customer_id').value;

    // Logs de los valores individuales antes de construir el JSON
    console.log("Customer ID:", customerId);
    console.log("Amount:", amount);
    console.log("Credit Type (Symbol):", symbol);
    console.log("Operation Type:", type);

    const transactionData = {
        customerId: customerId,
        amount: amount,
        creditType: symbol,
        operation: type
    };

    // Log del JSON final que se enviará
    console.log("Transaction Data JSON:", JSON.stringify(transactionData));

    fetch(`http://localhost:8080/credit/updateCryptoBalance/${customerId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(transactionData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error during transaction');
        }
        return response.json();
    })
    .then(data => {
        alert('Transaction successful!');
        // Lógica para recargar artículos y créditos
        window.location.reload(); // Recarga la página para reflejar cambios
    })
    .catch(error => {
        alert('Error: ' + error.message);
    });
}