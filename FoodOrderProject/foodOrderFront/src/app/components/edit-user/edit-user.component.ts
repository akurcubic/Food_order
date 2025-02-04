import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../service/user.service";
import {PermissionService} from "../../service/permission.service";
import {User} from "../../../model";

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit{

  editUserForm!: FormGroup;
  permiss: string[] = [];
  userId!: string;
  formBuilder: FormBuilder;

  constructor(formBuilder: FormBuilder, private router: Router, private userService: UserService, private permissService: PermissionService, private route: ActivatedRoute) {

    this.formBuilder = formBuilder;

    this.editUserForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      permissions: [[]]
    });
  };

  ngOnInit() {
    this.userId = this.route.snapshot.paramMap.get('id') || '';
    this.permissService.getAllPermissions().subscribe({
      next: (p: any[]) => {
        this.permiss = p.map(per => per.name);
        console.log("Upesno dohvatanje svih permisija! ",p);
      },
      error: (err: any) => {

        console.log("Greska pri dohvacanju permisija! ", err);
      }
    })

    this.userService.getUserById(Number.parseInt(this.userId)).subscribe({
      next: (u: User) => {

        const userPerm = u.permissions.map(p => p.name);
        this.editUserForm = this.formBuilder.group({

          firstName: [u.firstName, Validators.required],
          lastName: [u.lastName, Validators.required],
          email: [u.email, Validators.required],
          permissions: [userPerm, Validators.required]
        })
      },
      error: (err: any) => {

        console.log("Greska pri dohvacanju korisnika preko id! ", err);
      }
    })

  };

  onPermissionChange(permission: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const currentPermissions: String[] = this.editUserForm.get('permissions')?.value || [];

    if (isChecked) {
      this.editUserForm.patchValue({
        permissions: [...currentPermissions, permission],
      });
    } else {

      this.editUserForm.patchValue({
        permissions: currentPermissions.filter((perm) => perm !== permission),
      });
    }
    console.log("Permisije novog korisnika nakon promena: ", this.editUserForm.value.permissions);
  }

  editUser(){

    if(this.editUserForm.valid){

      const userData = {

        firstName: this.editUserForm.value.firstName,
        lastName: this.editUserForm.value.lastName,
        password: "Sifra se ovde ne menja!",
        email: this.editUserForm.value.email,
        permissions: this.editUserForm.value.permissions
      };

      this.userService.editUser(userData,Number.parseInt(this.userId)).subscribe({

        next: (user: User) => {

          console.log("Korisnik je uspesno promenjen!", user);
          window.alert("Uspena promena podataka!");
          const userFromLS = JSON.parse(localStorage.getItem('user') || '{}');

          if(Number.parseInt(this.userId) === Number.parseInt(userFromLS.userId)){
            console.log("Proveravam sebe");
            const updatedUser = {
              userId: user.id,
              firstName: user.firstName,
              lastName: user.lastName,
              email: user.email,
              permissions: user.permissions.map((p: any) => p.name),

            };
            localStorage.removeItem('user');
            localStorage.setItem('user', JSON.stringify(updatedUser));
          }
          this.router.navigate(['/home']);
        },
        error: (err: any) => {

          window.alert(err.error);
        }
      })
    }
  }

}
