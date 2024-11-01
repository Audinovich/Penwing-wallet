document.addEventListener('DOMContentLoaded', function() {
    const walletId = document.getElementById('wallet_id').value;
    const customerId = document.getElementById('customer_id').value;

    if (walletId) {
        // Llamada a la API con el customerId correctamente
        fetch(`http://localhost:8080/credit/articleCreditInfo/${customerId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('No se pudo obtener los datos');
            }
            return response.json();
        })
        .then(articleCreditInfo => {
            displayArticlesAndCredit(articleCreditInfo);
        })
        .catch(error => {
            console.error('Error al obtener los datos:', error);
        });
    } else {
        console.error('No se encontró el walletId en el HTML.');
    }
});

function displayArticlesAndCredit(articleCreditInfo) {
    const walletArticlesDiv = document.getElementById('wallet-articles');
    const euroValueElement = document.getElementById('euro-value');
    walletArticlesDiv.innerHTML = '';
    let totalEuros = 0;

    // Si hay información, tomamos el euroBalance
    if (articleCreditInfo.length > 0) {
        totalEuros = articleCreditInfo[0].euroBalance || 0; // Aquí se toma el euroBalance del primer artículo
    }

    // Itera sobre cada artículo y agrega su información
    articleCreditInfo.forEach((article, index) => {
        const articleRow = `
            <tr>
                <td>${index + 1}</td>
                <td>${article.symbol}</td>
                <td><img src="${article.image}" alt="${article.name}" width="50"></td>
                <td>${article.name}</td>
                <td>${article.creditAmount || 'N/A'}</td>
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

    confirmTransaction(amount, type, symbol);
});

function confirmTransaction(amount, type, symbol) {
    const customerId = document.getElementById('customer_id').value;

    const transactionData = {
        customerId: customerId,
        amount: amount,
        creditType: symbol,
        operation: type
    };

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
        window.location.reload();
    })
    .catch(error => {
        alert('Error: ' + error.message);
    });
}

function openCreditModal() {
    $('#creditModal').modal('show');
}

document.getElementById('confirm-credit-load').addEventListener('click', function() {
    const amount = parseFloat(document.getElementById('credit-amount').value);
    const customerId = document.getElementById('customer_id').value;

    if (isNaN(amount) || amount <= 0) {
        alert("Por favor, ingrese un monto válido.");
        return;
    }

    const creditData = {
        amount: amount
    };

    fetch(`http://localhost:8080/credit/addEuroCredit/${customerId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(creditData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al cargar crédito');
        }
        return response.json();
    })
    .then(data => {
        alert('Crédito cargado exitosamente!');
        $('#creditModal').modal('hide');
        window.location.reload();
    })
    .catch(error => {
        alert('Error: ' + error.message);
    });
});