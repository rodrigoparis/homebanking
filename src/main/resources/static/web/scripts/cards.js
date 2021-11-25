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

