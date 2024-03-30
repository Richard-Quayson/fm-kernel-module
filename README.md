### Simple OS Kernel
The repository contains three branches.

* **main**: contains working code on progress
* **richard**: contains test implementation to be merged with main when it works.
* **thomas**: contains test implementation to be merged with main when it works.

**Pushing rule of thumb**:

Always pull before pushing. More specifically:
* pull in the main branch

```[bash]
git switch main
git pull
````

* switch to your test branch

```[bash]
git switch <branch_name>
````

* merge main with your branch 

```[bash]
git merge main
````

* resolve all conflicts, commit and then push to your origin branch
```[bash]
git push
````

* create pull request on GitHub and merge when **able to merge** is shown on the Pull request page.


**Let's have fun while passing the course! 🙏😂🥳😁🙏**