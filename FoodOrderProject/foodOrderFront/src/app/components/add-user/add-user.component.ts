import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../service/user.service";
import {User} from "../../../model";
import {PermissionService} from "../../service/permission.service";

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit{

  addUserForm: FormGroup;
  permiss: string[] = [];


  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService, private permissService: PermissionService) {

    this.addUserForm = this.formBuilder.group({

      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', Validators.required],
      permissions: [[], Validators.required]
    })
  };

  ngOnInit() {

    this.permissService.getAllPermissions().subscribe({
      next: (p: any[]) => {
        this.permiss = p.map(per => per.name);
        console.log("Upesno dohvatanje svih permisija! ",p);
      },
      error: (err: any) => {

        console.log("Greska pri dohvacanju permisija! ", err.error);
      }
    })
  };

  onPermissionChange(permission: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const currentPermissions: String[] = this.addUserForm.get('permissions')?.value || [];

    if (isChecked) {
      this.addUserForm.patchValue({
        permissions: [...currentPermissions, permission],
      });
    } else {

      this.addUserForm.patchValue({
        permissions: currentPermissions.filter((perm) => perm !== permission),
      });
    }
    console.log("Permisije novog korisnika nakon promena: ", this.addUserForm.value.permissions);
  }

  addUser(){

    if(this.addUserForm.valid){

      const userData = {

        firstName: this.addUserForm.value.firstName,
        lastName: this.addUserForm.value.lastName,
        password: this.addUserForm.value.password,
        email: this.addUserForm.value.email,
        permissions: this.addUserForm.value.permissions
      };

      this.userService.addUser(userData).subscribe({

        next: (user: User) => {
          console.log("Korisnik je uspesno dodat!", user);
          window.alert("Korisnik je uspesno kreiran!");
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.log("Greska prilikom dodavanja korisnika!", err.error);
          window.alert(err.error);
        }
      })
    }
  }



}
