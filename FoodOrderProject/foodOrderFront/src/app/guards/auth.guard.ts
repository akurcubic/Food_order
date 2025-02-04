import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {


  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const userPermissions = user.permissions;
  console.log("Korisnikove permisije u gardu: ", userPermissions);

  const requiredPer = route.data['permissions'];
  console.log("Potrebne permisije permisije u gardu: ", requiredPer);

  if(requiredPer && requiredPer.every((perm: string) => userPermissions.includes(perm))){
    return true;
  }
  window.alert("You do not have permission to access this page.");
  return false;

};
