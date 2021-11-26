
const app = Vue.createApp({
    data() {
        return {
            clientData: {
                accounts: []
            },
            accountsLink: "",
            first_name: "",
            last_name: "",
            email: "",
            chart: "",
            series: "",
            pieData: [],
            savings: 0,
            currentAccount: 0,
            creationDate: "",
            isLoan: true,
            clientId: 0,
            hasCards: false,
            payOutData: {
                accountNumber: "",
                clientLoanId: "",
                paymentQuantity: ""
            },
            quantityAux: 0,
            selectedAccountId: ""
        }
    },
    created() {
        this.getClientId();
        this.createPie();
        this.loadData();
    },

    methods: {
        checkLoan() {
            this.isLoan = this.clientData.clientLoans.length > 0
        },
        getClientId() {
            const urlSearchParams = new URLSearchParams(window.location.search);
            this.clientID = urlSearchParams.get("client_id")
        },
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.clientData = response.data
                    this.first_name = this.clientData.first_name
                    this.last_name = this.clientData.last_name
                    response.data.clientLoans.sort((a, b) => parseInt(a.clientLoan_id - b.clientLoan_id))
                    response.data.accounts.forEach(element => {
                        this.pieData.push(
                            {
                                "account": element.number,
                                "balance": element.balance
                            }
                        )
                        this.savings += element.balance;
                    });
                    if (this.clientData.cards.length > 0) {
                        this.hasCards = true;
                    }
                    this.chart.data = this.pieData
                    this.checkLoan();
                    setTimeout(() => {
                        this.spinnerOut();
                    }, 3500);
                })
                .catch(e => {
                    
                })
        },
        spinnerOut(){   
            document.getElementsByClassName("spinnerContainer")[0].classList.add("d-none")    
            this.$refs.nav.classList.remove("d-none")
            this.$refs.main.classList.remove("d-none")
            this.$refs.footer.classList.remove("d-none")
            this.$refs.header.classList.remove("d-none")
        },
        currentDate() {
            const current = new Date();
            const date = `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`;
            return date;
        },
        createPie() {
            am4core.useTheme(am4themes_animated);
            this.chart = am4core.create("chartdiv", am4charts.PieChart);
            this.series = this.chart.series.push(new am4charts.PieSeries());
            this.series.dataFields.value = "balance";
            this.series.dataFields.category = "account";
            // this creates initial animation
            this.series.hiddenState.properties.opacity = 1;
            this.series.hiddenState.properties.endAngle = -90;
            this.series.hiddenState.properties.startAngle = -90;
            this.series.ticks.template.disabled = true;
            this.series.alignLabels = false;
            //{category}\n
            this.series.labels.template.text = "${value}";
            this.series.labels.template.radius = am4core.percent(-40);
            this.series.labels.template.fill = am4core.color("black");
            // this.chart.legend = new am4charts.Legend();
        },
        setCurrent(account) {
            this.currentAccount = account.balance;
        },
        openingDate(account) {
            let dt = new Date(account.creationDate)
            return account.creationDate.slice(0, 10)
        },
        getID(account) {
            return `./account.html?id=${account.account_id}&client_id=${this.clientData.id}`

        },
        logOut() {         
            axios.post('/api/logout')
                .then(response => {
                    window.location.href = "./index.html";
                })
                .catch(error => {
                    console.log(error);
                })
        },
        createAccount() {
            axios.post('/api/clients/current/accounts')
                .then(response => {
                    swal("That's great!", "We've created a new account for you!", "success")
                        .then(response => {
                            window.location.href = "./accounts.html";
                        })

                })
                .catch(error => {
                    console.log(error.message);
                    swal("We're sorry", error.response.data, "info");

                })
        },
        payOutFx(id, remainingPayments) {
            this.payOutData.clientLoanId = id
            this.quantityAux = remainingPayments
        },
        payLoan() {
            console.log("YENDO A PAGAR");
            axios.post('/api/loans/payOut', `accountNumber=${this.payOutData.accountNumber}&clientLoanID=${this.payOutData.clientLoanId}&quantity=${this.payOutData.paymentQuantity}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(e => {
                    console.log(e.data);
                    this.successMessage(e.data)
                }).catch(e => {
                    this.errorMessage(e.response.data)
                })
        },
        errorMessage(message) {
            swal({
                title: "Something went wrong",
                text: message,
                icon: "info",
                buttons: ["Check Loan Process"],
                dangerMode: true,
            }).then((e) => {
                if (e) {
                    window.location.href = "./loan-application.html"
                }
            })
        },
        successMessage(message) {
            let e = this.payOutData.accountNumber;
            this.selectedAccountId = this.clientData.accounts.find(account => account.number == e).account_id
            swal({
                title: message,
                text: `Would you like to go to your Account N°${this.payOutData.accountNumber} page?`,
                icon: "success",
                buttons: ["No, thank you", "Yes, please"],
                dangerMode: false,
            }).then((e) => {
                if (e) {
                    window.location.href = `./account.html?id=${this.selectedAccountId}`
                } else {
                    location.reload()
                }
            })
        },
        selectAccount(e) {
            this.selectedAccountId = this.clientData.accounts.find(account => account.accountNumber == e).account_id
        },
    },
    computed: {
        maxAccounts() {
            return this.clientData.accounts.length < 3
        },
        hasGraphics(){
            return !this.savings > 0
        }

    }
})
const debug = app.mount("#app")

