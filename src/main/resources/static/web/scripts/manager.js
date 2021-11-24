const app = Vue.createApp({
    data() {
        return {
            errors: [],
            users: [],
            json: [],
            first_name: "",
            last_name: "",
            email: ""
        }
    },
    created() {
        this.loadData();
    },
    methods: {
        loadData() {
            axios.get('/rest/clients')
                .then(response => {
                    this.json = response.data;
                    this.users = response.data._embedded.clients
                })
                .catch(e => {
                    
                })
        },
        postClient() { 
            axios.post('/rest/clients', {
                "first_name": this.first_name,
                "last_name": this.last_name,
                "email": this.email
            }).then(response => {
                this.loadData();
            }).catch(e => {
                ;
            })
        },
        addClient() {
            this.errors = [];
            if (!this.first_name) {
                this.errors.push("First Name");
            }
            if (!this.last_name) {
                this.errors.push('Last Name');
            }
            if (!this.email) {
                this.errors.push('El correo electrónico es obligatorio.');
            } else if (!this.validEmail(this.email)) {
                this.errors.push('El correo electrónico debe ser válido.');
            }
            if (!this.errors.length) {
                this.postClient();
                return true;
            } else {
                alertify.alert('Missing Data', `Missing data '${this.errors.toString()}'`);
            }
        },
        validEmail: function (email) {
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        },
        deleteClient(client) {
            if (confirm('Do you really want to delete this Client?')) {
                axios.delete(client._links.self.href)
                    .then(response => {
                        this.loadData();
                    })                   
            }
        }
    }
})
const debug = app.mount("#app")