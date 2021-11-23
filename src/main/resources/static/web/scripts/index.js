
const app = Vue.createApp({
  data() {
    return {
      notIn: false,
      email: "",
      password: "",
      su_first_name: "",
      su_last_name: "",
      su_email: "",
      su_password: "",
      errorMessage: "",
      inUseMail: false,
      validMail: true,
      validMailLogIn: true,
      missingData: false,
      clientId: 2,
      signUpElements: [],
      missingEmail: false,
      missingPassword: false,
      isPasswordVisible: false
    }
  },
  computed: {
    checkMail() {
      if (this.su_email == "") {
        this.inUseMail = false
        return this.su_email == ""

      }
    },
    showPassword() {
      if (this.isPasswordVisible) {
        return "text";
      } else {
        return "password";
      }
    }
  },
  methods: {
    enableSignInButton() {
      if (this.email != "" && this.password != "") {
        return false
      } else {
        return true
      }
    },
    enableSignUpButton() {
      this.signUpElements = [this.su_first_name, this.su_last_name, this.su_email, this.su_password]
      if (this.signUpElements.every(element => element !== "")) {
        return false
      } else {
        return true
      }
    },
    signUp(e) {
      e.preventDefault()
      if (!this.validEmail(this.su_email)) {
        this.validMail = false
        return
      } else {
        this.validMail = true
      }
      swal({
        title: "We will send you an email to validate your account",
        text: "This could take a few seconds!",
        icon: "info",
        buttons: ["Got it!"],
        dangerMode: false,
      })
      this.createClient()
    },
    createClient() {
      axios.post('/api/clients', `first_name=${this.su_first_name}&last_name=${this.su_last_name}&email=${this.su_email}&password=${this.su_password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(response => {
          this.email = this.su_email
          this.password = this.su_password
          swal("Done!", {
            title: "Please, check your email and click on the link to enable your account",
            buttons: "Got it!",
            icon: "success"
          }).then((value) => {
            console.log(value)
            if (value) {
              window.location.href = "./index.html"
            }
          })
        })
        .catch(error => {
          console.log(error.response.data)
          if (error.response.status == 403) {
            if (error.response.data == "Email already in use")
              this.inUseMail = true
          }

        })
    },
    login(e) {
      if (e) {
        e.preventDefault()
      }
      if (!this.validEmail(this.email)) {
        this.validMailLogIn = false
        return
      } else {
        this.validMailLogIn = true
      }

      axios.post('/api/login', `email=${this.email}&password=${this.password}`, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
        .then(response => {
          if (this.email == "admin@admin.com") {
            window.location.href = "../h2-console"
          } else {
            window.location.href = "./accounts.html";
          }
          console.log(response)
        })
        .catch(error => {
          if (error.response.status == 404) {
            this.errorMessage = "User not found, please check your email"
          } else if (error.response.status == 418) {
            this.errorMessage = "Account not enabled, please confirm your email address"
          } else if (error.response.status == 401) {
            this.errorMessage = "Can not log in, please check your password"
          } else {
            this.errorMessage = error.response.data
          }
          this.notIn = true;
        })
    },
    logout() {
      axios.post('/api/logout')
        .then(response => {
        })
        .catch(error => {
          console.log('Error', error.message);
        })
    },
    validEmail(email) {
      var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },
  }
})

app.mount("#app")



const sign_in_btn = document.querySelector("#sign-in-btn");
const sign_up_btn = document.querySelector("#sign-up-btn");
const container = document.querySelector(".container");

sign_up_btn.addEventListener("click", () => {
  container.classList.add("sign-up-mode");
});

sign_in_btn.addEventListener("click", () => {
  container.classList.remove("sign-up-mode");
});
