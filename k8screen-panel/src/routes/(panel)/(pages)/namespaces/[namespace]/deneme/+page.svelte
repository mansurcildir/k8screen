<script lang="ts">
  import { onMount } from 'svelte';
  import { userAPI } from '$lib/service/user-service';
  import type { UserItem } from '$lib/model/user/UserItem';

  let user: UserItem;
  let socket: WebSocket;
  let namespace = 'imentis';
  let podName = 'imentis-backend-b86c48bd5-4dcxq';

  let terminalOutput: string[] = [];
  let userInput: string = '';

  // Kullanıcı profilini al
  onMount(async () => {
    user = await userAPI.getProfile();
    const wsUrl = `ws://localhost:8080/ws/exec?namespace=${namespace}&podName=${podName}&userId=${user.id}`;

    // WebSocket bağlantısını kur
    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
      console.log('WebSocket bağlantısı kuruldu');
    };

    socket.onmessage = (event) => {
      const message = event.data;
      terminalOutput.push(message);
    };

    socket.onerror = (error) => {
      console.error('WebSocket hatası:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket bağlantısı kapandı');
    };
  });

  // Kullanıcıdan gelen komutu WebSocket'e gönder
  const sendCommand = () => {
    if (socket && socket.readyState === WebSocket.OPEN && userInput.trim() !== '') {
      socket.send(userInput);
      terminalOutput.push(`> ${userInput}`);
      userInput = '';
    }
  };
</script>

<style>
  .terminal {
    background-color: #111;
    color: #00ff00;
    font-family: monospace;
    padding: 20px;
    width: 80%;
    margin: auto;
    border: 2px solid #00ff00;
  }

  .output {
    white-space: pre-wrap;
    margin-bottom: 10px;
  }

  input {
    background-color: #111;
    color: #00ff00;
    border: none;
    width: 100%;
    font-size: 16px;
    outline: none;
    padding: 8px;
  }

  input::placeholder {
    color: #00ff00;
  }
</style>

<main>
  <div class="terminal">
    <div class="output">
      {#each terminalOutput as line}
        <div>{line}</div>
      {/each}
    </div>
    <input
      type="text"
      bind:value={userInput}
      on:keydown={(e) => e.key === 'Enter' && sendCommand()}
      placeholder="Komut girin..."
    />
  </div>
</main>
