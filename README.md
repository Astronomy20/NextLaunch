# NextLaunch

NextLaunch is a simple launcher that keeps all your projects and games in one place so you can find and run them easily.

The goal is to remove friction between building something and actually launching it.

---

## What It Does

- Keeps your local projects organized in a single folder  
- Lets you launch projects with one click  
- Connects to selected public GitHub repositories  
- Allows downloading projects directly from inside the launcher  
- Bundles the required runtime so users don’t need to install anything extra  

---

## How It Works

NextLaunch scans a dedicated `projects` folder.  
Each project has its own folder with a small configuration file that tells the launcher how to run it.

You can also tag specific GitHub repositories so they appear inside the launcher and can be downloaded directly.

---

## Project Structure

Each project should follow this structure:

```bash
ProjectName/
 ├── project.json
 ├── executable file (exe / jar / script)
 └── other resources
```

### Example `project.json`
```bash
{
  "id": "myproject-id"
  "name": "My Project",
  "version": "1.0.0",
  "author": "YourName",
  "description": "Short description here.",
  "entryPoint": "myproject.exe"
}
```

The launcher reads this file to display information and determine how to run the project.

---

## GitHub Integration

To make a repository available inside NextLaunch, add the topic:

nextlaunch-compatible

Only public repositories with this topic will be shown in the launcher.

---

## Why NextLaunch

NextLaunch was created as a clean and practical way to centralise projects and games in one place, making them easy to manage and launch.
