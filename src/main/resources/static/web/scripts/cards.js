const app = Vue.createApp({
    data() {
        return {
            clientData: [],
            isLoan: true,
            debitCards: [],
            creditCards: [],

        }
    },
    created() {
        this.loadData();
    },
    methods: {
        deleteWarn(cardNumber) {
            swal({
                title: "WARNING INFORMATION",
                text: `If you agree you wont be able to see your card N° ${cardNumber} anymore. The final deletion is subject to review by our financial agents.`,
                icon: "info",
                buttons: ["Decline", "Accept"],
                dangerMode: true,
            }).then((e) => {
                if (e) {
                    console.log("A BORRAR CUENTA")
                    this.deleteCard(cardNumber)
                }
            })
        },
        deleteCard(cardNumber) {
            console.log("yendo a back");
            axios.post('/api/cards/delete', `cardNumber=${cardNumber}`)
                .then(response => {
                    swal({
                        title: "DONE!",
                        text: `Your card number ${cardNumber} is no long available. Fully deletion upon agent report`,
                        icon: "info",
                        buttons: "Ok",
                        dangerMode: false,
                    }).then((e) => {
                        if (e) {
                            window.location.href = "./cards.html"
                        }
                    })
                }).catch(error => console.log(error))
        },
        spinnerOut(){   
            document.getElementsByClassName("spinnerContainer")[0].classList.add("d-none")    
            this.$refs.nav.classList.remove("d-none")
            this.$refs.main.classList.remove("d-none")
            this.$refs.footer.classList.remove("d-none")
            this.$refs.header.classList.remove("d-none")
        },
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.clientData = response.data
                    this.debitCards = this.clientData.cards.filter(card => card.type == 'DEBIT')
                    this.creditCards = this.clientData.cards.filter(card => card.type == 'CREDIT')
                    setTimeout(() => {
                        this.spinnerOut();
                    }, 1000);
                })
                .catch(e => {
                    
                })
        },
        currentDate() {
            const current = new Date();
            const date = `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`;
            return date;
        },
        thruDate(card) {
            let year = card.thruDate.slice(2, 4)
            let month = card.thruDate.slice(5, 7)
            return `${month}/${year}`
        },
        getID(account) {
            return `./account.html?id=${account.account_id}&client_id=${this.clientData.id}`
        },
        map(card) {
            let id;
            card.cardColor == 'GOLD' ? id = 1 : card.cardColor == "SILVER" ? id = 2 : id = 3
            return `./assets/images/map${id}.png`
        },
        logOut() {
            axios.post('/api/logout')
                .then(response => {
                    console.log('signed out!!!')
                })
                .catch(error => {
                    console.log(error);
                })
        },
        isOverdue(date) {
            const today = new Date();
            const thruDate = new Date(date)  
           
            return today > thruDate
        }
    }
})
const debug = app.mount("#app")

