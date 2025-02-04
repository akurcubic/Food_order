import {Component, OnInit} from '@angular/core';
import {User} from "../../../model";
import {UserService} from "../../service/user.service";

@Component({
  selector: 'app-all-users',
  templateUrl: './all-users.component.html',
  styleUrls: ['./all-users.component.css']
})
export class AllUsersComponent implements OnInit{

  users: User[] = [];
  errorMessage = '';
  canDeleteUser = false;
  canEditUser = false;

  constructor(private userService: UserService) {
  }

  ngOnInit() {

    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {

        this.users = users;
        console.log("Uspesno dohvaceni svi korisnici: " + this.users);

        const user = JSON.parse(localStorage.getItem('user') || '{}');

        this.canDeleteUser = user.permissions.includes("can_delete_users");
        this.canEditUser = user.permissions.includes("can_update_users");
      },
      error: (error: any) => {

        console.log("Greska prilikom dohvatanja korisnika: " + error);
        this.errorMessage = "Greska prilikom dohvatanja korisnika";
      }
    })
  }

  deleteUser(id: number){

    this.userService.deleteUser(id).subscribe({
      next: (res: any) => {
        console.log("Kornisk je uspesno obrisan");
        this.users = this.users.filter(u => u.id != id);
      },

      error: (err: any) => {
        console.log("Greska prilikom brisanja korisnika!");
        this.errorMessage = "Greska prilikom brisanja korisnika";
      }
    })
  }


}
